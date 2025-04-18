package com.devumut.DearDiary.controllers;

import com.devumut.DearDiary.domain.dto.TotalStatisticsDto;
import com.devumut.DearDiary.domain.entities.DiaryEmotionEntity;
import com.devumut.DearDiary.domain.entities.TotalDiaryStatisticsEntity;
import com.devumut.DearDiary.exceptions.TokenNotValidException;
import com.devumut.DearDiary.jwt.JwtUtil;
import com.devumut.DearDiary.mappers.Mapper;
import com.devumut.DearDiary.services.StatisticsService;
import com.devumut.DearDiary.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final Mapper<TotalDiaryStatisticsEntity, TotalStatisticsDto> mapper;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService, Mapper<TotalDiaryStatisticsEntity, TotalStatisticsDto> mapper, JwtUtil jwtUtil, TokenService tokenService) {
        this.statisticsService = statisticsService;
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @GetMapping("/total-diary-statistics")
    public ResponseEntity<?> getTotalDiaryStatistics(
            @RequestHeader("Authorization") String token
    ) {
        token = jwtUtil.extractTokenFromHeader(token);
        if (!tokenService.isTokenValid(token)) {
            throw new TokenNotValidException("Token is not valid.");
        }
        UUID userId = jwtUtil.extractUserId(token);
        TotalDiaryStatisticsEntity statisticsEntity = statisticsService.getTotalDiaryStatistics(userId);

        return new ResponseEntity<>(mapper.mapTo(statisticsEntity), HttpStatus.OK);
    }

    @GetMapping("/total-emotion-statistics")
    public ResponseEntity<?> getTotalEmotionStatistics(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "all") String timeRange
    ) {
        token = jwtUtil.extractTokenFromHeader(token);
        if (!tokenService.isTokenValid(token)) {
            throw new TokenNotValidException("Token is not valid.");
        }
        UUID userId = jwtUtil.extractUserId(token);

        List<DiaryEmotionEntity> list = statisticsService.getTotalEmotionCounts(userId, timeRange);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
