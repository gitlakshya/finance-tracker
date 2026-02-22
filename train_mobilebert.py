#!/usr/bin/env python3
"""
MobileBERT SMS Transaction Classifier Training Script
Uses TFLite Model Maker to train a MobileBERT-based binary text classifier.

Dataset: refined_training_data.csv
- Message column: raw SMS text
- Is_Debit column: 1 = debit transaction, 0 = not a debit transaction

Output: model.tflite (place in app/src/main/assets/)
"""

# Install with: pip install tflite-model-maker
from tflite_model_maker import text_classifier
from tflite_model_maker.text_classifier import DataLoader
from tflite_model_maker.text_classifier import ModelSpec
import os

def train():
    print("=== MobileBERT SMS Transaction Classifier ===")
    print("Loading MobileBERT specification...")
    
    # 1. Load the MobileBERT Transformer specification
    mb_spec = ModelSpec.get('mobilebert_logistic')

    # 2. Load the refined dataset
    print("Loading dataset from refined_training_data.csv...")
    data = DataLoader.from_csv(
        filename='refined_training_data.csv',
        text_column='Message',
        label_column='Is_Debit',
        model_spec=mb_spec,
        is_training=True
    )

    print(f"Total samples: {len(data)}")
    train_data, test_data = data.split(0.8)
    print(f"Train: {len(train_data)}, Test: {len(test_data)}")

    # 3. Train the Transformer (Transformers need far fewer epochs than basic models)
    print("Training MobileBERT model (3 epochs)...")
    model = text_classifier.create(train_data, model_spec=mb_spec, epochs=3)

    # 4. Evaluate
    print("Evaluating model...")
    loss, acc = model.evaluate(test_data)
    print(f"MobileBERT Accuracy: {acc * 100:.2f}%")
    print(f"MobileBERT Loss: {loss:.4f}")

    # 5. Export .tflite file
    assets_dir = 'app/src/main/assets'
    os.makedirs(assets_dir, exist_ok=True)
    model.export(export_dir=assets_dir, export_format=[text_classifier.ExportFormat.TFLITE])
    print(f"Model exported to {assets_dir}/model.tflite")
    print("=== Training Complete ===")

if __name__ == '__main__':
    train()
