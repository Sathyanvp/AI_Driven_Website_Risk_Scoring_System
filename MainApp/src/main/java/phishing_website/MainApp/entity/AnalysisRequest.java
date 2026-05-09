package phishing_website.MainApp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class AnalysisRequest {
	   
	    private String url;
	
	//URL features
		private Integer url_length;
		private Integer token_count;
	    private Integer hyphenated_domain;
	    private Integer uses_ip_address;
	    private Integer uses_shortener;
	    private Double char_entropy;
	    private Double ngram_entropy;
	    //DOM features
	    private Integer form_count;
	    private Integer password_field_present;
	    private Integer external_form_action;
	    private Integer iframe_count;
	    //Behavioral features
	    private Integer redirect_indicator;
//	    private Integer possible_js_obfuscation;
	    
	    
		//Handles missing values
		public float[] featuretoVector() {
			return new float[] {
					//URL String features
		            url_length != null ? url_length : 0,
		            token_count != null ? token_count : 0,
		            hyphenated_domain != null ? hyphenated_domain : 0,
		            uses_ip_address != null ? uses_ip_address : 0,
		            uses_shortener != null ? uses_shortener : 0,
		            char_entropy != null ? char_entropy.floatValue() : 0,
		            ngram_entropy != null ? ngram_entropy.floatValue() : 0,
		            //DOM features
		            form_count != null ? form_count : 0,
		            password_field_present != null ? password_field_present : 0,
		            external_form_action != null ? external_form_action : 0,
		            iframe_count != null ? iframe_count : 0,
		            //Behavioral features
		            redirect_indicator != null ? redirect_indicator : 0,
//		            possible_js_obfuscation != null ? possible_js_obfuscation : 0,
		         
		        };
		}


		public AnalysisRequest() {
			
		}
	    
	
}
