package phishing_website_detector.Application.entity;


import java.util.List;





import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnalysisResponse {
	
	private float risk_score;
	private String risk_level;
	private List<String> reasons;
	private long timestamp;
	private String Url;

	 public AnalysisResponse(float risk_score, String risk_level, List<String> reasons) {
	        this.risk_score = risk_score;
	        this.risk_level = risk_level;
	        this.reasons = reasons;
	        this.timestamp = System.currentTimeMillis();
	    }

	 public void setUrl(String url) {
		this.Url = url;
		
	 }

	 public void setTimestamp(long timeMillis) {
		this.timestamp=timeMillis;
		
	 }

	 
 
	
	
	
	



}
