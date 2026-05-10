# AI-Driven Website Risk Scoring System

An intelligent, real-time phishing detection system that leverages Machine Learning to evaluate website safety. This project combines a **Java Spring Boot** backend with an **XGBoost** model to provide instant risk scores based on URL and DOM features.

## 🚀 Overview

This system protects users from phishing attacks by analyzing website characteristics in real-time. It features a dual-component architecture:
1.  **Chrome Extension:** Acts as the sensor, extracting features from the user's active browser tab.
2.  **Spring Boot Backend:** Acts as the brain, processing features through an **ONNX**-integrated machine learning model to return a risk probability score.

## ✨ Key Features

* **Real-Time Detection:** Instant analysis of URLs and page content as you browse.
* **AI-Powered Inference:** Uses a high-performance XGBoost model exported via ONNX for fast, cross-platform inference in Java.
* **Behavioral Analysis:** Goes beyond simple blacklists by analyzing DOM elements and URL structures.
* **Modular API:** A RESTful backend that can be easily integrated with other security tools.

## 🛠️ Tech Stack

### Backend
* **Language:** Java 17+
* **Framework:** Spring Boot 3.x
* **Build Tool:** Maven
* **ML Integration:** ONNX Runtime (Java)

### Machine Learning
* **Model:** XGBoost
* **Libraries:** Scikit-learn, Pandas (Data Preprocessing)
* **Format:** ONNX (Open Neural Network Exchange)

### Extension
* **Languages:** JavaScript, HTML, CSS
* **APIs:** Chrome Extension API (Manifest v3)

## 📁 Project Structure

```text
AI_Driven_Website_Risk_Scoring_System/
├── extension/                 # Chrome extension source code
│   ├── manifest.json          # Extension configuration
│   ├── content.js             # DOM feature extraction logic
│   └── popup/                 # Extension UI
└── Website-risk-scoring-system/
    ├── src/main/java/         # Spring Boot application source
    │   └── phishing_website_detector/
    │       ├── controller/    # REST Endpoints
    │       ├── service/       # ML Inference & Logic
    │       └── model/         # Data Transfer Objects (DTOs)
    └── src/main/resources/    # Config & ONNX model files
```




## Extension Setup
Open Google Chrome and navigate to chrome://extensions/.

Click Load unpacked.

Select the frontend folder from the cloned repository.

## 📊 How It Works
Extraction: The Chrome extension scrapes features such as URL length, presence of '@' symbols, number of subdomains, and suspicious DOM elements (e.g., hidden forms).

Transmission: The features are sent via a POST request to the Spring Boot /api/analyze endpoint.

Inference: The backend loads the pre-trained .onnx model and feeds the input features into the inference engine.

Result: The system returns a risk score (0 to 10). The extension UI updates to show  "Medium Risk", or "High Risk" based on the probability with Explaination.
