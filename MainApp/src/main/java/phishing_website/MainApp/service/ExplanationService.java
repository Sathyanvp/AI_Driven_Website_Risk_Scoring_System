package phishing_website.MainApp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;



import phishing_website.MainApp.entity.AnalysisRequest;

@Service
public class ExplanationService {

    public List<String> generateExplanations(AnalysisRequest request, float riskScore) {
        List<String> reasons = new ArrayList<>();
        
        if (riskScore < 4) {
            // Low risk - minimal explanations
            return reasons;
        }
        
        // Analyze URL features
        analyzeURLFeatures(request, reasons, riskScore);
        
        // Analyze entropy features
        analyzeEntropyFeatures(request, reasons);
        
        // Analyze DOM security features
        analyzeDOMFeatures(request, reasons);
        
        // Analyze behavioral features
//        analyzeBehaviorFeatures(request, reasons);
        
        return reasons;
    }
    
    /**
     * Analyze URL-based suspicious indicators
     */
    private void analyzeURLFeatures(AnalysisRequest request, List<String> reasons, double riskScore) {
        // IP address detection
        if (request.getUses_ip_address() != null && request.getUses_ip_address() == 1) {
            reasons.add("Website uses IP address instead of domain name");
        }
        
        // URL shortener detection
        if (request.getUses_shortener() != null && request.getUses_shortener() == 1) {
            reasons.add("URL uses shortener service (may hide actual destination)");
        }
        
        // Domain with hyphens
        if (request.getHyphenated_domain() != null && request.getHyphenated_domain() == 1) {
            reasons.add("Domain contains unusual hyphens (mimicking legitimate domains)");
        }
        
        // Unusually long URL
        if (request.getUrl_length() != null && request.getUrl_length() > 80) {
            reasons.add("Suspicious URL length: " + request.getUrl_length() + " characters");
        }
        
        // High token count (many path segments)
        if (request.getToken_count() != null && request.getToken_count() > 20) {
            reasons.add("URL contains unusually many path segments");
        }
    }
    
    /**
     * Analyze entropy-based suspicious indicators
     */
    private void analyzeEntropyFeatures(AnalysisRequest request, List<String> reasons) {
        // High character entropy suggests obfuscation
        if (request.getChar_entropy() != null && request.getChar_entropy() > 4.5) {
            reasons.add("URL tokens show high randomness (potential obfuscation)");
        }
        
      
        
        // High n-gram entropy
        if (request.getNgram_entropy() != null && request.getNgram_entropy() > 5.0) {
            reasons.add("URL character patterns are highly unusual");
        }
    }
    
    /**
     * Analyze DOM security features
     */
    private void analyzeDOMFeatures(AnalysisRequest request, List<String> reasons) {
        // Credential form detected
        if (request.getPassword_field_present() != null && request.getPassword_field_present() == 1) {
            reasons.add("Credential form detected (password field present)");
        }
        
       
        
        // External form submission
        if (request.getExternal_form_action() != null && request.getExternal_form_action() == 1) {
            reasons.add("Form submits to external domain (not same site)");
        }
        
        // Multiple forms
        if (request.getForm_count() != null && request.getForm_count() > 3) {
            reasons.add("Multiple forms on page (" + request.getForm_count() + " detected)");
        }
        
        // Suspicious iframes
        if (request.getIframe_count() != null && request.getIframe_count() > 2) {
            reasons.add("Multiple embedded iframes detected (" + request.getIframe_count() + ")");
        }
        
        // Redirect indicators
        if (request.getRedirect_indicator() != null && request.getRedirect_indicator() == 1) {
            reasons.add("Page contains redirect indicators");
        }
    }
    
    /**
     * Analyze JavaScript behavior features
     */
//    private void analyzeBehaviorFeatures(AnalysisRequest request, List<String> reasons) {
//        // Obfuscated JavaScript
//        if (request.getPossible_js_obfuscation() != null && request.getPossible_js_obfuscation() == 1) {
//            reasons.add("JavaScript code appears to be obfuscated");
//        }
//        
//        
//    }

}
