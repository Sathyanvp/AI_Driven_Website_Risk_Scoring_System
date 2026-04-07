package phishing_website.MainApp.service;


import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import phishing_website.MainApp.entity.AnalysisRequest;
import phishing_website.MainApp.entity.AnalysisResponse;

import phishing_website.MainApp.util.FeatureNormalizer;


@Service
@Slf4j
public class AnalyzerService {
	
	
	    private FeatureNormalizer normalizer;
	    private ONNXModelService model;
	    private ExplanationService explanationService;
	    

	    public AnalyzerService(ONNXModelService model, FeatureNormalizer normalizer, ExplanationService explanationService) {

		this.model = model;
		this.normalizer = normalizer;
		this.explanationService = explanationService;
	}


	public AnalysisResponse analyzeUrl(AnalysisRequest request) {
		log.info("analyzeurl method got request");
		try {
			
//			AnalysisRequest normalizedrequest = normalizer.normalize(request);
//			log.info("request normalized");
			float[] features_vector = request.featuretoVector();

			Double probability = model.predict(features_vector);
			log.info("prediction done: " +probability );
			
			float risk_score = convertProbabilityToRiskScore(probability);
			log.info("converted to risk score");
			
			String risk_level = determineRiskLevel(risk_score);
			log.info("risk_level is determined" + risk_level);
			
			List<String> explaination = explanationService.generateExplanations(request, risk_score);
			log.info("explaination generated");
			
			AnalysisResponse response = new AnalysisResponse(risk_score,
                    risk_level,
                    explaination);
			response.setUrl(request.getUrl());
            response.setTimestamp(System.currentTimeMillis());
            log.info("response created");
			return  response;
		}
		 catch (Exception e) {
	            log.error("Analysis failed for URL: {}", request.getUrl(), e);
	            throw new RuntimeException("Analysis failed: " + e.getMessage(), e);
	        }
	}

	private String determineRiskLevel(float risk_score) {
		 if (risk_score < 4.0) {
	            return "LOW";
	        } else if (risk_score < 7.0) {
	            return "MEDIUM";
	        } else {
	            return "HIGH";
	        }
		
	}

	private float convertProbabilityToRiskScore(Double probability) {
		
		return (float) (probability * 10.0);
	}

}
