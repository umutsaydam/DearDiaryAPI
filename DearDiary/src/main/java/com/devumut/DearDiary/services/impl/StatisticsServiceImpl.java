package com.devumut.DearDiary.services.impl;

import com.devumut.DearDiary.domain.entities.DiaryEmotionEntity;
import com.devumut.DearDiary.domain.entities.TotalDiaryStatisticsEntity;
import com.devumut.DearDiary.repositories.StatisticsRepository;
import com.devumut.DearDiary.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticsRepository;

    @Autowired
    public StatisticsServiceImpl(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public TotalDiaryStatisticsEntity getTotalDiaryStatistics(UUID userId) {
        Object[] result = statisticsRepository.getTotalStatistics(userId);

        int totalDiaries = result[0] != null ? ((Number) result[0]).intValue() : 0;
        int currentStreak = result[1] != null ? ((Number) result[1]).intValue() : 0;
        int longestStreak = result[2] != null ? ((Number) result[2]).intValue() : 0;

        return new TotalDiaryStatisticsEntity(totalDiaries, currentStreak, longestStreak);
    }

    @Override
    public List<DiaryEmotionEntity> getTotalEmotionCounts(UUID userId, String timeRange) {
        List<Object[]> result = statisticsRepository.getTotalEmotions(userId, timeRange);
        List<DiaryEmotionEntity> list = new ArrayList<>();

        for (Object[] row : result) {
            int emotionId = ((Number) row[0]).intValue();
            int count = ((Number) row[1]).intValue();
            list.add(new DiaryEmotionEntity(emotionId, count));
        }

        return list;
    }

}
