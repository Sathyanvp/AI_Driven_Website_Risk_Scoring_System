/**
 * popup.js - Popup UI Controller
 * 
 * Manages the extension popup interface, displaying analysis results
 * and providing user settings management.
 */

document.addEventListener('DOMContentLoaded', function() {
    loadLastAnalysis();
    loadSettings();
    setupEventListeners();
});

/**
 * Load and display the last analysis result
 */
function loadLastAnalysis() {
    chrome.storage.local.get(['lastAnalysis'], function(result) {
        const analysis = result.lastAnalysis;
        const container = document.getElementById('analysis-result');
        
        if (analysis) {
            displayAnalysisResult(analysis);
        } else {
            // No analysis available
            container.innerHTML = `
                <div class="no-analysis">
                    No analysis available.<br>
                    Visit a website to see results.
                </div>
            `;
        }
    });
}

/**
 * Display analysis result in the popup
 */
function displayAnalysisResult(analysis) {
    const container = document.getElementById('analysis-result');
    
    let riskClass = 'risk-low';
    if (analysis.risk_score >= 7) {
        riskClass = 'risk-high';
    } else if (analysis.risk_score >= 4) {
        riskClass = 'risk-medium';
    }
    
    container.innerHTML = `
        <div class="risk-score ${riskClass}">
            ${analysis.risk_score.toFixed(1)}/10
        </div>
        <div style="text-align: center; margin-bottom: 10px; font-weight: bold;">
            ${analysis.risk_level} RISK
        </div>
        
        <div class="reasons">
            <strong>Reasons:</strong>
            <ul>
                ${analysis.reasons.map(reason => `<li>${reason}</li>`).join('')}
            </ul>
        </div>
        
        <div style="margin-top: 10px; font-size: 11px; color: #666;">
            Analyzed: ${new Date(analysis.timestamp).toLocaleString()}
        </div>
    `;
}

/**
 * Load user settings from storage
 */
function loadSettings() {
    chrome.storage.sync.get({
        apiEndpoint: 'http://localhost:8080/api/analyze',
        enabled: true,
        showLowRisk: false
    }, function(settings) {
        document.getElementById('api-endpoint').value = settings.apiEndpoint;
        document.getElementById('enabled').checked = settings.enabled;
        document.getElementById('show-low-risk').checked = settings.showLowRisk;
    });
}

/**
 * Setup event listeners for settings changes
 */
function setupEventListeners() {
    // API endpoint change
    document.getElementById('api-endpoint').addEventListener('change', function() {
        const endpoint = this.value;
        chrome.storage.sync.set({ apiEndpoint: endpoint });
    });
    
    // Enable/disable detection
    document.getElementById('enabled').addEventListener('change', function() {
        const enabled = this.checked;
        chrome.storage.sync.set({ enabled: enabled });
        
        // Notify content scripts
        chrome.tabs.query({}, function(tabs) {
            tabs.forEach(tab => {
                chrome.tabs.sendMessage(tab.id, { action: 'toggleDetection', enabled: enabled });
            });
        });
    });
    
    // Show low risk warnings
    document.getElementById('show-low-risk').addEventListener('change', function() {
        const showLowRisk = this.checked;
        chrome.storage.sync.set({ showLowRisk: showLowRisk });
    });
}