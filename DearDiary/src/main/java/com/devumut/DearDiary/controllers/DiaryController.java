package com.devumut.DearDiary.controllers;

import com.devumut.DearDiary.domain.dto.DiaryDto;
import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.exceptions.DiaryAlreadyExistException;
import com.devumut.DearDiary.exceptions.DiaryNotFoundException;
import com.devumut.DearDiary.exceptions.TokenNotValidException;
import com.devumut.DearDiary.jwt.JwtUtil;
import com.devumut.DearDiary.mappers.Mapper;
import com.devumut.DearDiary.services.DiaryService;
import com.devumut.DearDiary.services.EmotionPredictService;
import com.devumut.DearDiary.services.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final EmotionPredictService emotionPredictService;
    private final DiaryService diaryService;
    private final Mapper<DiaryEntity, DiaryDto> mapper;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Autowired
    public DiaryController(EmotionPredictService emotionPredictService, DiaryService diaryService, Mapper<DiaryEntity, DiaryDto> mapper, JwtUtil jwtUtil, TokenService tokenService) {
        this.emotionPredictService = emotionPredictService;
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
        if ((diaryDto.getDiary_date() == null && diaryService.isExistDiaryByDate(new Date())) ||
                (diaryDto.getDiary_date() != null && diaryService.isExistDiaryByDate(diaryDto.getDiary_date()))) {
            throw new DiaryAlreadyExistException("Diary was already added.");
        }

        DiaryEntity diaryEntity = mapper.mapFrom(diaryDto);

        if(diaryEntity.getDiary_emotion() == -1){
            int predictedEmotion = emotionPredictService.predictEmotionFromText(diaryEntity.getDiary_content());
            diaryEntity.setDiary_emotion(predictedEmotion);
        }

        if (diaryEntity.getDiary_date() == null) {
            diaryEntity.setDiary_date(new Date());
        }

        UUID userId = jwtUtil.extractUserId(token);
        UserEntity user = new UserEntity();
        user.setUser_id(userId);
        diaryEntity.setUser(user);
        DiaryEntity savedDiaryEntity = diaryService.saveDiary(diaryEntity);
        DiaryDto savedDiaryDto = mapper.mapTo(savedDiaryEntity);

        return new ResponseEntity<>(savedDiaryDto, HttpStatus.CREATED);
    }

    @GetMapping("/get-diary/{id}")
    public ResponseEntity<?> getDiary(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") UUID diaryId
    ) {
        token = jwtUtil.extractTokenFromHeader(token);
        if (!tokenService.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<DiaryEntity> optionalDiaryEntity = diaryService.getDiaryById(diaryId);
        if (optionalDiaryEntity.isEmpty()) {
            throw new DiaryNotFoundException("Diary not found with the provided ID.");
        }
        DiaryEntity diaryEntity = optionalDiaryEntity.get();
        DiaryDto diaryDto = mapper.mapTo(diaryEntity);
        return new ResponseEntity<>(diaryDto, HttpStatus.OK);
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

    @DeleteMapping("/delete-diary/{id}")
    public ResponseEntity<HttpStatus> deleteDiary(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") UUID diaryId
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
            throw new DiaryNotFoundException("Diary not found with the provided ID.");
        }
        DiaryEntity oldDiaryEntity = optionalDiaryEntity.get();
        if (newDiaryEntity.getDiary_emotion() == oldDiaryEntity.getDiary_emotion()) {
            int predictedEmotion = emotionPredictService.predictEmotionFromText(newDiaryEntity.getDiary_content());
            newDiaryEntity.setDiary_emotion(predictedEmotion);
        }

        DiaryEntity updatedDiaryEntity = diaryService.updateDiary(newDiaryEntity);
        DiaryDto updatedDiaryDto = mapper.mapTo(updatedDiaryEntity);

        return new ResponseEntity<>(updatedDiaryDto, HttpStatus.OK);
    }
}
