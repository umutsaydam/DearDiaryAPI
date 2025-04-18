package com.devumut.DearDiary.repositories;

import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.domain.entities.TotalDiaryStatisticsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiaryRepository extends CrudRepository<DiaryEntity, UUID> {

    @Query("SELECT d FROM DiaryEntity d WHERE d.user.user_id = ?1")
    List<DiaryEntity> getAllDiaries(UUID userId);

    @Query("SELECT d FROM DiaryEntity d WHERE FUNCTION('DATE', d.diary_date) = ?1")
    Optional<DiaryEntity> getDiaryByDate(LocalDate date);

    @Query(value = """
        WITH user_data AS (
          SELECT DISTINCT DATE(diary_date) AS diary_date
          FROM user_diaries
          WHERE user_id = ?1
        ),

        -- Current Streak
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
              WHEN previousDate IS NULL OR diary_date - previousDate > 1 THEN 1
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

        -- The Current Streak
        LatestStreak AS (
          SELECT
            CASE
              WHEN NOT EXISTS (
                SELECT 1 FROM user_diaries WHERE user_id = ?1 AND DATE(diary_date) = CURRENT_DATE
              ) AND NOT EXISTS (
                SELECT 1 FROM user_diaries WHERE user_id = ?1 AND DATE(diary_date) = CURRENT_DATE - INTERVAL '1 day'
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
        """, nativeQuery = true)
    List<Object[]> getTotalStatistics(UUID userId);


}
