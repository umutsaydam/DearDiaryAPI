package com.devumut.DearDiary.controllers;

import com.devumut.DearDiary.domain.dto.DiaryDto;
import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.exceptions.TokenNotValidException;
import com.devumut.DearDiary.jwt.JwtUtil;
import com.devumut.DearDiary.mappers.Mapper;
import com.devumut.DearDiary.services.DiaryService;
import com.devumut.DearDiary.services.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final String flaskUrl = "http://flask-app:5000";
    private final WebClient.Builder webClientBuilder;
    private final DiaryService diaryService;
    private final Mapper<DiaryEntity, DiaryDto> mapper;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Autowired
    public DiaryController(WebClient.Builder webClientBuilder, DiaryService diaryService, Mapper<DiaryEntity, DiaryDto> mapper, JwtUtil jwtUtil, TokenService tokenService) {
        this.webClientBuilder = webClientBuilder;
        this.diaryService = diaryService;
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @PostMapping("/save-diary")
    public ResponseEntity<?> saveDiary(
            @RequestHeader("Authorization") String token,
            @RequestBody DiaryDto diaryDto
    ) throws JsonProcessingException {
        token = jwtUtil.extractTokenFromHeader(token);
        if (!tokenService.isTokenValid(token)) {
            throw new TokenNotValidException("Token is not valid.");
        }

        DiaryEntity diaryEntity = mapper.mapFrom(diaryDto);

        String emotionResult = webClientBuilder.baseUrl(flaskUrl)
                .build()
                .post()
                .uri("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("text", diaryEntity.getDiary_content()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(emotionResult);
        int predictedEmotion = root.get("predicted_label").asInt();

        UUID userId = jwtUtil.extractUserId(token);
        UserEntity user = new UserEntity();
        user.setUser_id(userId);
        diaryEntity.setUser(user);
        diaryEntity.setDiary_emotion(emotionResult == null ? 0 : predictedEmotion);
        DiaryEntity savedDiaryEntity = diaryService.saveDiary(diaryEntity);
        DiaryDto savedDiaryDto = mapper.mapTo(savedDiaryEntity);

        return new ResponseEntity<>(savedDiaryDto, HttpStatus.CREATED);
    }

    @GetMapping("/get-diaries")
    public ResponseEntity<?> getDiaries(@RequestHeader("Authorization") String token) {
        token = jwtUtil.extractTokenFromHeader(token);
        if (!tokenService.isTokenValid(token)) {
            throw new TokenNotValidException("Token is not valid.");
        }

        UUID userId = jwtUtil.extractUserId(token);
        List<DiaryEntity> list = diaryService.getAllDiariesByUserId(userId);
        List<DiaryDto> listDto = list.stream().map(entity ->
                new DiaryDto(entity.getDiary_id(), entity.getDiary_date(), entity.getDiary_content(), entity.getDiary_emotion())
        ).collect(Collectors.toList());
        return new ResponseEntity<>(listDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete-diary")
    public ResponseEntity<HttpStatus> deleteDiary(
            @RequestHeader("Authorization") String token,
            @RequestParam("id") UUID diaryId
    ) {
        token = jwtUtil.extractTokenFromHeader(token);
        if (!tokenService.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        diaryService.deleteDiary(diaryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/update-diary")
    public ResponseEntity<?> updateDiary(
            @RequestHeader("Authorization") String token,
            @RequestBody DiaryDto diaryDto
    ) throws JsonProcessingException {
        token = jwtUtil.extractTokenFromHeader(token);
        if (!tokenService.isTokenValid(token)) {
            throw new TokenNotValidException("Token is not valid.");
        }

        DiaryEntity newDiaryEntity = mapper.mapFrom(diaryDto);
        Optional<DiaryEntity> optionalDiaryEntity = diaryService.getDiaryById(newDiaryEntity.getDiary_id());

        if (optionalDiaryEntity.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        DiaryEntity oldDiaryEntity = optionalDiaryEntity.get();
        if (newDiaryEntity.getDiary_emotion() == oldDiaryEntity.getDiary_emotion()) {
            String emotionResult = webClientBuilder.baseUrl(flaskUrl)
                    .build()
                    .post()
                    .uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("text", newDiaryEntity.getDiary_content()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(emotionResult);
            int predictedEmotion = root.get("predicted_label").asInt();
            newDiaryEntity.setDiary_emotion(predictedEmotion);
        }

        DiaryEntity updatedDiaryEntity = diaryService.updateDiary(newDiaryEntity);
        DiaryDto updatedDiaryDto = mapper.mapTo(updatedDiaryEntity);

        return new ResponseEntity<>(updatedDiaryDto, HttpStatus.OK);
    }
}
