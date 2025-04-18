package com.devumut.DearDiary.repositories;

import java.util.List;
import java.util.UUID;

public interface StatisticsRepository {
    Object[] getTotalStatistics(UUID userId);
    List<Object[]> getTotalEmotions(UUID userId, String timeRange);
}