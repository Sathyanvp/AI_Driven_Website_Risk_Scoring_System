/**
 * background.js - Central controller (ONLY place that calls backend)
 */

const analyzedTabs = new Set();

// On install
chrome.runtime.onInstalled.addListener(() => {
    console.log('[Phishing Detector] Installed');

    chrome.storage.sync.set({
        apiEndpoint: 'http://localhost:8080/api/analyze',
        enabled: true,
        showLowRisk: false
    });
});

// SINGLE message listener (fixed)
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {

    if (request.action === 'getSettings') {
        chrome.storage.sync.get({
            apiEndpoint: 'http://localhost:8080/api/analyze',
            enabled: true,
            showLowRisk: false
        }, sendResponse);
        return true;
    }
});

// Trigger ONLY on page load
chrome.tabs.onUpdated.addListener((tabId, changeInfo, tab) => {

	if (
	    changeInfo.status === "complete" &&
	    tab.url &&
	    tab.url.startsWith("http") &&
	    !isIgnoredPage(tab.url)
	) {

        // prevent duplicate calls
        if (analyzedTabs.has(tabId)) return;
        analyzedTabs.add(tabId);

        console.log("[Detector] Analyzing:", tab.url);

        chrome.tabs.sendMessage(tabId, { action: "extractFeatures" }, async (features) => {

            if (chrome.runtime.lastError || !features) {
                console.warn("Feature extraction failed");
                return;
            }

            try {
                const response = await fetch('http://localhost:8080/api/analyze', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(features)
                });

                const result = await response.json();

                chrome.tabs.sendMessage(tabId, {
                    action: "showResult",
                    data: result
                });

            } catch (error) {
                console.error("API Error:", error);
            }
        });
    }
});


function isIgnoredPage(url) {

    try {
        const hostname = new URL(url).hostname;

        // Block search engines & internal pages
        const blockedDomains = [
            "google.com",
            "www.google.com",
            "bing.com",
            "search.yahoo.com"
        ];

        if (blockedDomains.some(domain => hostname.includes(domain))) {
            return true;
        }

        // Block Chrome internal pages
        if (url.startsWith("chrome://") || url.startsWith("edge://")) {
            return true;
        }

        // Block new tab / blank
        if (url === "about:blank") {
            return true;
        }

        return false;

    } catch {
        return true; // treat invalid URLs as ignored
    }
}

// Reset tracking when tab reloads
chrome.tabs.onUpdated.addListener((tabId, changeInfo) => {
    if (changeInfo.status === "loading") {
        analyzedTabs.delete(tabId);
    }
});

chrome.tabs.onRemoved.addListener((tabId) => {
    analyzedTabs.delete(tabId);
});