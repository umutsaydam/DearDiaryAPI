package com.devumut.DearDiary.domain.entities;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalDiaryStatisticsEntity {
    @Column(name = "total_diaries")
    private int totalDiaries;

    @Column(name = "current_streak")
    private int currentStreak;

    @Column(name = "longest_streak")
    private int longestStreak;
}

