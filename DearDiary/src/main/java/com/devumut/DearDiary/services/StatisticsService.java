package com.devumut.DearDiary.services;

import com.devumut.DearDiary.domain.entities.DiaryEmotionEntity;
import com.devumut.DearDiary.domain.entities.TotalDiaryStatisticsEntity;

import java.util.List;
import java.util.UUID;

public interface StatisticsService {
    TotalDiaryStatisticsEntity getTotalDiaryStatistics(UUID userId);
    List<DiaryEmotionEntity> getTotalEmotionCounts(UUID userId, String timeRange);
}
