package com.devumut.DearDiary.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalStatisticsDto {
    int totalDiaries;
    int currentStreak;
    int longestStreak;
}
