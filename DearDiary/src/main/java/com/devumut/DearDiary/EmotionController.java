package com.devumut.DearDiary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/predict")
public class EmotionController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String flaskUrl = "http://flask-app:5000/predict";
    private Map<String, String> payload;

    @PostMapping("/predict-emotion")
    public Mono<String> predictEmotion(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");

        return webClientBuilder.baseUrl(flaskUrl)
                .build()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"text\": \"" + text + "\"}")
                .retrieve()
                .bodyToMono(String.class);
    }
}
