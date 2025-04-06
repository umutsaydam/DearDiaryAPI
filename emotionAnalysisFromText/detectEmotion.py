#from transformers import BertTokenizer, BertForSequenceClassification
import torch
from datasets import load_dataset
from transformers import AutoTokenizer
#, AutoModelForSequenceClassification, TrainingArguments, Trainer
#import numpy as np
#import evaluate
#from transformers import BertTokenizer

model_ckpt = "distilbert-base-uncased"
tokenizer = AutoTokenizer.from_pretrained(model_ckpt)

model = torch.load("model/bert_emotion_model.pt", map_location=torch.device("cpu"), weights_only=False)
model.eval()

text = "I feel sad and alone."

inputs = tokenizer(text, return_tensors="pt", padding=True, truncation=True)
with torch.no_grad():
    outputs = model(**inputs)
    logits = outputs.logits
    predicted_class_id = torch.argmax(logits, dim=1).item()

# Emotion label'ları örneğin şöyleyse:
labels = ['sadness', 'joy', 'love', 'anger', 'fear', 'surprise']

print("Tahmin edilen duygu:", labels[predicted_class_id])