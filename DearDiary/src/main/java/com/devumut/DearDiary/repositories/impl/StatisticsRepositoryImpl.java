package com.devumut.DearDiary.repositories.impl;


import com.devumut.DearDiary.repositories.StatisticsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class StatisticsRepositoryImpl implements StatisticsRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Object[] getTotalStatistics(UUID userId) {
        return (Object[]) entityManager
                .createNativeQuery("""
                        WITH user_data AS (
                            SELECT DISTINCT CAST(diary_date AS DATE) AS diary_date
                            FROM user_diaries
                            WHERE user_id = ?1
                        ),
                        
                        PreviousDates AS (
                            SELECT
                                o.diary_date,
                                (
                                    SELECT MAX(u.diary_date)
                                    FROM user_data u
                                    WHERE u.diary_date < o.diary_date
                                ) AS previousDate
                            FROM user_data o
                        ),
                        
                        StreakGroups AS (
                            SELECT
                                diary_date,
                                CASE
                                    WHEN previousDate IS NULL OR (diary_date - previousDate) > 1 THEN 1
                                    ELSE 0
                                END AS group_change
                            FROM PreviousDates
                        ),
                        
                        StreakGroupsWithRunningSum AS (
                            SELECT
                                diary_date,
                                SUM(group_change) OVER (ORDER BY diary_date) AS group_num
                            FROM StreakGroups
                        ),
                        
                        SessionsWithRowNumber AS (
                            SELECT
                                diary_date,
                                ROW_NUMBER() OVER (ORDER BY diary_date) AS rn
                            FROM user_data
                        ),
                        
                        StreakGroupsMax AS (
                            SELECT
                                diary_date,
                                diary_date - rn * INTERVAL '1 day' AS streak_id
                            FROM SessionsWithRowNumber
                        ),
                        
                        StreakCounts AS (
                            SELECT COUNT(*) AS streak_count
                            FROM StreakGroupsMax
                            GROUP BY streak_id
                        ),
                        
                        LatestStreak AS (
                            SELECT
                                CASE
                                    WHEN NOT EXISTS (
                                        SELECT 1 FROM user_diaries 
                                        WHERE user_id = ?1 
                                        AND CAST(diary_date AS DATE) = CURRENT_DATE
                                    ) AND NOT EXISTS (
                                        SELECT 1 FROM user_diaries 
                                        WHERE user_id = ?1 
                                        AND CAST(diary_date AS DATE) = CURRENT_DATE - INTERVAL '1 day'
                                    )
                                    THEN 0
                                    ELSE (
                                        SELECT COUNT(*)
                                        FROM StreakGroupsWithRunningSum
                                        WHERE group_num = (
                                            SELECT group_num
                                            FROM StreakGroupsWithRunningSum
                                            WHERE diary_date = (
                                                SELECT MAX(diary_date)
                                                FROM user_data
                                                WHERE diary_date <= CURRENT_DATE
                                            )
                                        )
                                    )
                                END AS current_streak
                        )
                        
                        SELECT
                            (SELECT COUNT(*) FROM user_diaries WHERE user_id = ?1) AS total_diaries,
                            (SELECT current_streak FROM LatestStreak) AS current_streak,
                            (SELECT MAX(streak_count) FROM StreakCounts) AS longest_streak;
                        """)
                .setParameter(1, userId)
                .getSingleResult();
    }

    @Override
    public List<Object[]> getTotalEmotions(UUID userId, String timeRange) {
        StringBuilder sql = new StringBuilder("""
                    SELECT diary_emotion, COUNT(*)
                    FROM user_diaries
                    WHERE user_id = ?1
                """);

        LocalDateTime startDate = null;

        switch (timeRange.toLowerCase()) {
            case "this_week" -> {
                startDate = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
            }
            case "last_week" -> {
                startDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY).atStartOfDay();
                sql.append(" AND diary_date >= ?2 AND diary_date < ?3");
            }
            case "this_month" -> {
                startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            }
            case "all" -> {
            }
            default -> throw new IllegalArgumentException("Invalid timeRange: " + timeRange);
        }

        sql.append(" ");

        if (!timeRange.equalsIgnoreCase("all") && !timeRange.equalsIgnoreCase("last_week")) {
            sql.append("AND diary_date >= ?2 ");
        }

        sql.append("""
                    GROUP BY diary_emotion
                    ORDER BY diary_emotion
                """);

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter(1, userId);

        if (startDate != null) {
            if (timeRange.equalsIgnoreCase("last_week")) {
                LocalDateTime endDate = startDate.plusWeeks(1);
                query.setParameter(2, startDate);
                query.setParameter(3, endDate);
            } else {
                query.setParameter(2, startDate);
            }
        }

        return query.getResultList();
    }

}

