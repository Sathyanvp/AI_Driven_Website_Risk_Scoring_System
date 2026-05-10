package phishing_website_detector.Application.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import phishing_website_detector.Application.entity.AnalysisRequest;
import phishing_website_detector.Application.entity.AnalysisResponse;

import phishing_website_detector.Application.service.AnalyzerService;

@RestController
@Slf4j
@RequestMapping("/api")
//@CrossOrigin(
//	    originPatterns = {
//	        "chrome-extension://*", // Matches any extension ID
//	        "http://localhost:[*]"  // Matches any port on localhost
//	    }, 
//	    allowedHeaders = "*",
//	    methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS},
//	    allowCredentials = "true"
//	)
public class PhishingURLController {
	


	static final RequestMethod[] REQUEST_METHODS = { };
	private AnalyzerService service;
	
	public PhishingURLController(AnalyzerService service) {
		this.service = service;
	}
	
	@PostMapping("/analyze")
	public ResponseEntity<AnalysisResponse> analyzeUrl(@RequestBody AnalysisRequest request) {
		
		log.info("request reached");
		
        log.info("Features: url = {}, url_len={}, Token_count={}, hyphenated_domain = {}, uses_ip_address ={}, uses_shortener = {}, "
        		+ "Char_entropy={}, n_gram entropy = {}, forms={}, password_field_present ={}, external_form_action = {}, "
        		+ "iframe_count = {}, redirect_indicator = {}",
        		request.getUrl(),
        		request.getUrl_length(),
        		request.getToken_count(),
        		request.getHyphenated_domain(),
                request.getUses_ip_address(),
                request.getUses_shortener(),
                request.getChar_entropy(),
                request.getNgram_entropy(),
                request.getForm_count(),
                request.getPassword_field_present(),
                request.getExternal_form_action(),
                request.getIframe_count(),
                request.getRedirect_indicator()
//                request.getPossible_js_obfuscation()
                );
      
		try {
			if(request.getUrl() == null || request.getUrl().isEmpty()) {
				log.info("Bad request: Request is empty");
				return ResponseEntity.badRequest().build();
			}
			AnalysisResponse response = service.analyzeUrl(request);
			log.info("Risk Score = {} ", response.getRisk_score());
			return ResponseEntity.ok(response);
			
		}
		catch(Exception e) {
			log.info(e + "internal server error");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		
	}

}
