"""
train_model.py - XGBoost Model Training

Train a phishing detection model using XGBoost classifier.
Accepts CSV file with features and labels, performs train/test split,
trains the model with hyperparameter tuning, and saves it for later export.

Usage:
    python train_model.py --data features.csv --output model.pkl --test-size 0.2

Features Expected (17 total):
    url_length, token_count, hyphenated_domain, uses_ip_address, uses_shortener,
    char_entropy, token_entropy, ngram_entropy,
    form_count, password_field_present, email_field_present, external_form_action,
    iframe_count, redirect_indicator,
    possible_js_obfuscation, status_bar_customized, right_click_disabled
    
Label: 0 (legitimate) or 1 (phishing)
"""
import os
from tkinter import _test
from sklearn.ensemble import RandomForestClassifier as rfc
from sklearn.metrics import confusion_matrix, classification_report
import seaborn as seaborn
import matplotlib.pyplot as matlib
import pandas as pd
import numpy as np
import xgboost as xgb
import onnxmltools
import onnxruntime as ort
from sklearn.model_selection import train_test_split
# FIX 1: Import FloatTensorType from onnxmltools to avoid namespace RuntimeError
from onnxmltools.convert.common.data_types import FloatTensorType




class PhishingModelTrainer:
    
    def __init__(self, test_size=0.2, random_state=42):
        
        self.test_size = test_size
        self.random_state = random_state
        self.model = None
        self.scaler = None
        self.session = None
        self.input_name = None
        self.X_train = None
        self.X_test = None
        self.y_train = None
        self.y_test = None
        self.FEATURE_ORDER = [
    "url_length", "token_count", "hyphenated_domain", "uses_ip_address", "is_shortened",
    "char_entropy","ngram_entropy", "form_count", "password_field_present", "external_form_action", "iframe_count", 
    "redirect_indicator"
]
        
    def load_data(self, data_path):
       
        df = pd.read_csv(data_path)
        
        # Separate features and label
        dataset = pd.read_csv(data_path)
        df = df[df['url_length'].astype(str) != 'url_length']

    # 3. Select Features and Labels
    # We use FEATURE_ORDER to ensure the index mapping remains consistent for Java
        X = df[self.FEATURE_ORDER].apply(pd.to_numeric, errors='coerce').fillna(0)
        y = df['label'].astype(int)
        print(X[:5])
        return X , y
    
    def split_data(self, X, y):
        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # FIX 3: Convert to NumPy and cast to float32
    # This fixes the "Unable to interpret feature names" and "f%d pattern" error
        self.X_train = self.X_train.values.astype(np.float32)
        self.X_test = self.X_test.values.astype(np.float32)
    
    def train_model(self):
       
        
        # XGBoost hyperparameters
        # params = {
        #     'objective': 'binary:logistic',
        #     'max_depth': 6,
        #     'learning_rate': 0.1,
        #     'n_estimators': 200,
        #     'subsample': 0.8,
        #     'colsample_bytree': 0.8,
        #     'random_state': self.random_state,
        #     'scale_pos_weight': 1
        # }
        
        self.model = rfc(
    n_estimators=250,
    max_depth=10,
    min_samples_split=5,
    min_samples_leaf=2,
    max_features="sqrt",
    bootstrap=True,
    class_weight="balanced",
    random_state=42,
    n_jobs=-1
)
        self.model.fit(
            self.X_train, self.y_train)
        
        
        
    def evaluate(self):
        y_pred = self.model.predict(self.X_test)
        cm = confusion_matrix(self.y_test, y_pred)

# Visualize the matrix
        seaborn.heatmap(cm, annot=True, fmt='d', cmap='Blues')
        matlib.xlabel('Predicted')
        matlib.ylabel('Actual')
        matlib.show()

        print(classification_report(self.y_test, y_pred))
    
    def save_model(self, output_path):
        print("Exporting model to ONNX format...")
        num_features = self.X_train.shape[1]
    
    # We use the FloatTensorType we imported from onnxmltools
        initial_types = [('float_input', FloatTensorType([None, num_features]))]
    
    # FIX 4: Use target_opset=15 to match the installed library support
        onnx_model = onnxmltools.convert_sklearn(
            self.model, 
            initial_types=initial_types,
            target_opset=15 
        )
        #onnx_model.graph.output[0].name = "probabilities"
    # 6. Save the model
        full_path = os.path.join(output_path, "phishing_randomforest.onnx")
       
        onnxmltools.utils.save_model(onnx_model, full_path)
        print(f"Model saved as {full_path}")



    def predict (self,features_list):
        print("hellooooo")
        model_onnx_path = r"C:\Users\ELCOT\git\Phishing_website_detector\MainApp\src\main\resources\model\phishing_xgboost.onnx"
        if self.session is None:
            if not os.path.exists(model_onnx_path):
                return f"Error: Model file not found at {model_onnx_path}"
            self.session = ort.InferenceSession(model_onnx_path)
            self.input_name = self.session.get_inputs()[0].name

        input_data = np.array([features_list], dtype=np.float32)
        # Run inference: returns [labels, probabilities]
        labels, probabilities = self.session.run(None, {self.input_name: input_data})
        print(labels[0])
        return probabilities[0]
    

    def train(self, data_path, output_path):
      
        X, y = self.load_data(data_path)
        self.split_data(X, y)
        
        self.train_model()
        self.evaluate()
        # self.save_model(output_path)
#         sample_data = [
# 33,6,0,0,0,3.6430741894285696,3.6120568402659834,1,0,0,0,2,0]
#         print(self.predict(sample_data))


def main():
    np.set_printoptions(suppress=True, precision=5)
    onnx_path = r"MainApp\src\main\resources\model"
    dataset_path = r"MainApp\src\main\resources\model\final_dataset.csv"
    trainer = PhishingModelTrainer()
    trainer.train(dataset_path, onnx_path)
   

if __name__ == '__main__':
    main()
