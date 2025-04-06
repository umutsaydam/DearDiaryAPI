from flask import Flask, request, jsonify
from transformers import BertTokenizer, BertForSequenceClassification
import torch
from datasets import load_dataset
from transformers import AutoTokenizer

app = Flask(__name__)

#emotions = ['Sadness', 'Joy', 'Love', 'Anger', 'Fear', 'Surprise']

model_ckpt = "distilbert-base-uncased"
tokenizer = AutoTokenizer.from_pretrained(model_ckpt)
model = torch.load("model/bert_emotion_model.pt", map_location=torch.device("cpu"), weights_only=False)
model.eval()

def predict_emotion(text):
    inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True)
    with torch.no_grad():
        outputs = model(**inputs)
        probs = torch.nn.functional.softmax(outputs.logits, dim=1)
        predicted = torch.argmax(probs, dim=1).item()
    return predicted

@app.route("/predict", methods=["POST"])
def predict():
    data = request.json
    text = data.get("text", "")
    if not text:
        return jsonify({"error": "Text is required"}), 400
    
    label = predict_emotion(text)
    return jsonify({"predicted_label":label})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)