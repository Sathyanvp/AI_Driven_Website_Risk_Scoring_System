package phishing_website_detector.Application.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import ai.onnxruntime.OrtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;

/**
 * ONNXModelService - ONNX Runtime Model Inference Engine
 * 
 * Loads and manages the phishing detection ML model trained with XGBoost
 * and exported to ONNX format. Performs inference on extracted features.
 */
@Slf4j
@Service
public class ONNXModelService {
    
    @Value("${model.path}")
    private String modelPath;
    
    private OrtEnvironment environment;
    private OrtSession session;
    private static final String INPUT_NAME = "float_input";
    private static final String OUTPUT_NAME = "probabilities";
    
    /**
     * Initialize ONNX Runtime environment and load the model
     */
    @PostConstruct
    public void initialize() {
        try {
            log.info("Initializing ONNX Runtime environment...");
            this.environment = OrtEnvironment.getEnvironment();
            
            log.info("Loading phishing detection model from: {}", modelPath);
            this.session = environment.createSession(modelPath);
            
            log.info("Model loaded successfully. Input: {}, Output: {}", INPUT_NAME, OUTPUT_NAME);
        } catch (OrtException e) {
            log.error("Failed to initialize ONNX model", e);
            throw new RuntimeException("Failed to load ONNX model: " + e.getMessage(), e);
        }
    }
    
    /**
     * Perform inference on extracted features
     * 
     * @param request Analysis request containing features
     * @return Probability of phishing (0.0 - 1.0)
     */
    public double predict(float[] features_vector) {
        try {
            
            
            // Reshape to [1, 17] for batch processing (1 sample, 17 features)
            // long[] shape = new long[]{1, features.length};
            
            // Create ONNX tensor
            float[][] reshapedFeatures = new float[][]{features_vector};
            OnnxTensor tensor = OnnxTensor.createTensor(environment, reshapedFeatures);
            
            // Run inference
            Map<String, OnnxTensor> inputs = Collections.singletonMap(INPUT_NAME, tensor);
            var results = session.run(inputs);
            
            // Extract output
            OnnxTensor output = (OnnxTensor) results.get(1);
            OnnxTensor label = (OnnxTensor) results.get(0);
            long[] predictedLabel = (long[]) label.getValue();
            for(long i : predictedLabel) System.out.println(i);
            float[][] probabilities = (float[][]) output.getValue();

         

            // Return probability of phishing class (index 1)
//            double legitProb = probabilities[0][0];    // 0.09% sure it's safe
//            double phishingProb = probabilities[0][1];
            double phishingProbability = probabilities[0][1];
//            double phishingProbability = Math.min(legitProb, phishingProb);
            
        

            log.info("Predicted Probability: {}", probabilities[0][0] +", "+probabilities[0][1]);
            
            
            tensor.close();
            results.close();
            
            return phishingProbability;
            
        } catch (OrtException e) {
            log.error("Model inference failed", e);
            throw new RuntimeException("Model inference failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during prediction", e);
            throw new RuntimeException("Prediction error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cleanup resources
     */
    @PreDestroy
    public void cleanup() {
        try {
            if (session != null) {
                session.close();
                log.info("ONNX session closed");
            }
        } catch (OrtException e) {
            log.warn("Error closing ONNX session", e);
        }
    }
}