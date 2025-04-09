package com.devumut.DearDiary.services.impl;

import com.devumut.DearDiary.services.EmotionPredictService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmotionPredictServiceImpl implements EmotionPredictService {

//   If you are using docker please use this url
    private final String flaskUrl = "http://flask-app:5000";
//    If you are using local host please use this url
//    private final String flaskUrl = "http://127.0.0.1:5000";
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public EmotionPredictServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public int predictEmotionFromText(String text) {
        try {
            String emotionResult = webClientBuilder.baseUrl(flaskUrl)
                    .build()
                    .post()
                    .uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("text", text))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(emotionResult);

            return root.get("predicted_label").asInt();
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Error parsing emotion result");
        }
    }
}
