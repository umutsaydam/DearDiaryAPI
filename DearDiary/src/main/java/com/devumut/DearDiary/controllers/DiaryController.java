package com.devumut.DearDiary.controllers;

import com.devumut.DearDiary.domain.dto.DiaryDto;
import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.jwt.JwtUtil;
import com.devumut.DearDiary.mappers.Mapper;
import com.devumut.DearDiary.services.DiaryService;
import com.devumut.DearDiary.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;
    private final Mapper<DiaryEntity, DiaryDto> mapper;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Autowired
    public DiaryController(DiaryService diaryService, Mapper<DiaryEntity, DiaryDto> mapper, JwtUtil jwtUtil, TokenService tokenService) {
        this.diaryService = diaryService;
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @PostMapping("/save-diary")
    public ResponseEntity<?> saveDiary(
            @RequestHeader("Authorization") String token,
            @RequestBody DiaryDto diaryDto
    ) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!tokenService.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        DiaryEntity diaryEntity = mapper.mapFrom(diaryDto);
        try {
            UUID userId = jwtUtil.extractUserId(token);
            UserEntity user = new UserEntity();
            user.setUser_id(userId);
            diaryEntity.setUser(user);
            DiaryEntity savedDiaryEntity = diaryService.saveDiary(diaryEntity);
            DiaryDto savedDiaryDto = mapper.mapTo(savedDiaryEntity);

            return new ResponseEntity<>(savedDiaryDto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-diaries")
    public ResponseEntity<?> getDiaries(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!tokenService.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            UUID userId = jwtUtil.extractUserId(token);
            List<DiaryEntity> list = diaryService.getAllDiariesByUserId(userId);
            List<DiaryDto> listDto = list.stream().map(entity ->
                    new DiaryDto(entity.getDiary_id(), entity.getDiary_date(), entity.getDiary_content(), entity.getDiary_emotion())
            ).collect(Collectors.toList());
            return new ResponseEntity<>(listDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-diary")
    public ResponseEntity<HttpStatus> deleteDiary(
            @RequestHeader("Authorization") String token,
            @RequestParam("id") UUID diaryId
    ) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
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
    ) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!tokenService.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            DiaryEntity newDiaryEntity = mapper.mapFrom(diaryDto);
            DiaryEntity updatedDiaryEntity = diaryService.updateDiary(newDiaryEntity);
            DiaryDto updatedDiaryDto = mapper.mapTo(updatedDiaryEntity);

            return new ResponseEntity<>(updatedDiaryDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
