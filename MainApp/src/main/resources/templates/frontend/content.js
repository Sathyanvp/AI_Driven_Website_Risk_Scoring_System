/**
 * content.js - Passive Feature Extractor
 */

class FeatureExtractor {

    constructor() {
        this.features = {};
    }

    initialize() {
        console.log('[Detector] Content script ready');
    }

    extractAllFeatures() {
        this.features = {
            ...this.extractURLFeatures(),
            ...this.extractEntropyFeatures(),
            ...this.extractDOMFeatures(),
            ...this.extractBehaviorFeatures()
        };
        return this.features;
    }

    extractURLFeatures() {
        const url = window.location.href;
		

        return {
			url: url,
            url_length: url.length,
            token_count: url.replace(/^https?:\/\//, '').split(/[./\-?_=&]/).length,
            hyphenated_domain: this.hasHyphenatedDomain(url),
            uses_ip_address: this.usesIPAddress(url),
            uses_shortener: this.usesShortener(url)
        };
    }

    hasHyphenatedDomain(url) {
        try {
            return new URL(url).hostname.includes('-') ? 1 : 0;
        } catch { return 0; }
    }

    usesIPAddress(url) {
        try {
            return /^\d+\.\d+\.\d+\.\d+$/.test(new URL(url).hostname) ? 1 : 0;
        } catch { return 0; }
    }

    usesShortener(url) {
        const shorteners = ['bit\\.ly', 'goo\\.gl', 'shorte\\.st', 'go2l\\.ink', 'x\\.co', 'ow\\.ly', 't\\.co', 'tinyurl', 'tr\\.im', 
			'is\\.gd', 'cli\\.gs', 'yfrog\\.com', 'migre\\.me', 'ff\\.im', 'tiny\\.cc', 'url4\\.eu', 'twit\\.ac', 'su\\.pr', 'twurl\\.nl',
			 'snipurl\\.com', 'short\\.to', 'BudURL\\.com', 'ping\\.fm', 'post\\.ly', 'Just\\.as', 'bkite\\.com', 'snipr\\.com', 
			 'fic\\.kr', 'loopt\\.us', 'doiop\\.com', 'short\\.ie', 'kl\\.am', 'wp\\.me', 'rubyurl\\.com', 'om\\.ly', 'to\\.ly', 
			 'bit\\.do', 't\\.co', 'lnkd\\.in', 'db\\.tt', 'qr\\.ae', 'adf\\.ly', 'goo\\.gl', 'bitly\\.com', 'cur\\.lv', 
			 'tinyurl\\.com', 'ow\\.ly', 'bit\\.ly', 'ity\\.im', 'q\\.gs', 'is\\.gd', 'po\\.st', 'bc\\.vc', 'twitthis\\.com', 'u\\.to', 
			 'j\\.mp', 'buzurl\\.com', 'cutt\\.us', 'u\\.bb', 'yourls\\.org', 'x\\.co', 'prettylinkpro\\.com', 'scrnch\\.me', 
			 'filoops\\.info', 'vzturl\\.com', 'qr\\.net', '1url\\.com', 'tweez\\.me', 'v\\.gd', 'tr\\.im', 'link\\.zip\\.net'];
        try {
            const domain = new URL(url).hostname;
            return shorteners.some(s => domain.includes(s)) ? 1 : 0;
        } catch { return 0; }
    }

    extractEntropyFeatures() {
        const url = window.location.href;
		const urlNoScheme = url.replace(/^https?:\/\//, '');

        return {
            char_entropy: this.entropy(urlNoScheme),
            ngram_entropy: this.ngramEntropy(urlNoScheme, 3)
        };
    }

    entropy(str) {
        const map = {};
        for (let c of str) map[c] = (map[c] || 0) + 1;

        let e = 0;
        for (let k in map) {
            let p = map[k] / str.length;
            e -= p * Math.log2(p);
        }
        return e;
    }

    ngramEntropy(str, n) {
        const grams = {};
        for (let i = 0; i <= str.length - n; i++) {
            const g = str.substr(i, n);
            grams[g] = (grams[g] || 0) + 1;
        }

        let e = 0;
        const total = Object.values(grams).reduce((a, b) => a + b, 0);

        for (let k in grams) {
            let p = grams[k] / total;
            e -= p * Math.log2(p);
        }
        return e;
    }

    extractDOMFeatures() {
        return {
            form_count: document.forms.length,
            password_field_present: document.querySelector('input[type="password"]') ? 1 : 0,
            external_form_action: this.externalForm(),
            iframe_count: document.querySelectorAll('iframe').length
        };
    }

    externalForm() {
        const domain = location.hostname;
        for (let form of document.forms) {
            try {
                if (new URL(form.action).hostname !== domain) return 1;
            } catch { return 1; }
        }
        return 0;
    }

    extractBehaviorFeatures() {
        return {
			redirect_indicator: document.body.innerText.toLowerCase().includes("redirect") ? 1 : 0,
            possible_js_obfuscation: document.querySelectorAll("script").length > 20 ? 1 : 0,
           
        };
    }

    handleResult(result) {

        if (result.risk_score < 4) return;
		this.showWarningPopup(result)

   /*     alert(`⚠️ ${result.risk_level} RISK\nScore: ${result.risk_score}`);*/
    }
	
	showWarningPopup(result) {
	    // 1. Create the Host Element
	    const host = document.createElement('div');
	    host.id = 'phishing-detector-root';
	    document.documentElement.appendChild(host);

	    // 2. Attach Shadow DOM (creates the CSS 'fortress')
	    const shadow = host.attachShadow({ mode: 'closed' });

	    // 3. Define the Risk Theme
	    const isHighRisk = result.risk_level === 'HIGH';
	    const riskColor = isHighRisk ? '#eb1414' : '#ffc107';
	    const riskIcon = isHighRisk ? '🛑' : '⚠️';

	    // 4. Build the Template
	    const container = document.createElement('div');
	    container.innerHTML = `
	        <style>
	            .overlay {
	                position: fixed;
	                top: 0; left: 0;
	                width: 100vw; height: 100vh;
	                background: rgba(0, 0, 0, 0.85);
	                z-index: 2147483647;
	                display: flex;
	                align-items: center;
	                justify-content: center;
	                backdrop-filter: blur(4px);
	                font-family: system-ui, -apple-system, sans-serif;
	            }
	            .popup {
	                background: white;
	                border-radius: 12px;
	                padding: 24px;
	                max-width: 420px;
	                width: 90%;
	                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
	                animation: scaleIn 0.3s ease-out;
	            }
	            @keyframes scaleIn {
	                from { transform: scale(0.9); opacity: 0; }
	                to { transform: scale(1); opacity: 1; }
	            }
	            .header {
	                text-align: center;
	                margin-bottom: 20px;
	            }
	            .risk-title {
	                font-size: 22px;
	                font-weight: 800;
	                color: ${riskColor};
	                text-transform: uppercase;
	                margin-bottom: 8px;
	            }
	            .score {
	                font-size: 16px;
	                color: #444;
	            }
	            .reasons-box {
	                background: #f8f9fa;
	                border-left: 4px solid ${riskColor};
	                padding: 12px 16px;
	                margin-bottom: 20px;
	                border-radius: 4px;
	            }
	            .reasons-label {
	                font-weight: bold;
	                display: block;
	                margin-bottom: 8px;
	                color: #222;
	            }
	            ul {
	                margin: 0;
	                padding-left: 20px;
	                color: #444;
	            }
	            li { margin-bottom: 4px; line-height: 1.4; }
	            .actions {
	                display: flex;
	                gap: 12px;
	                justify-content: center;
	            }
	            button {
	                padding: 12px 20px;
	                border: none;
	                border-radius: 6px;
	                font-weight: 600;
	                cursor: pointer;
	                transition: opacity 0.2s;
	            }
	            button:hover { opacity: 0.9; }
	            #btn-continue { background: #e0e0e0; color: #333; }
	            #btn-leave { background: #0062ff; color: white; flex-grow: 1; }
	        </style>

	        <div class="overlay">
	            <div class="popup">
	                <div class="header">
	                    <div class="risk-title">${riskIcon} ${result.risk_level} RISK</div>
	                    <div class="score">Threat Score: <strong>${result.risk_score.toFixed(1)}/10</strong></div>
	                </div>
	                
	                <div class="reasons-box">
	                    <span class="reasons-label">Security Flags:</span>
	                    <ul>
	                        ${result.reasons.map(r => `<li>${r}</li>`).join('')}
	                    </ul>
	                </div>
	                
	                <div class="actions">
	                    <button id="btn-leave">Protect Me (Leave Site)</button>
	                    <button id="btn-continue">Continue Anyway</button>
	                </div>
	            </div>
	        </div>
			`;

	    shadow.appendChild(container);

	    // 5. Logic: Event Listeners
	    shadow.getElementById('btn-continue').addEventListener('click', () => {
	        host.remove();
	    });

	    shadow.getElementById('btn-leave').addEventListener('click', () => {
	        if (document.referrer) {
	            window.history.back();
	        } else {
	            window.location.href = "https://www.google.com";
	        }
	    });
	}
	
}

const extractor = new FeatureExtractor();
extractor.initialize();

// Message interface (CRITICAL)
chrome.runtime.onMessage.addListener((req, sender, sendResponse) => {

    if (req.action === "extractFeatures") {
        sendResponse(extractor.extractAllFeatures());
    }

    if (req.action === "showResult") {
        extractor.handleResult(req.data);
    }
});