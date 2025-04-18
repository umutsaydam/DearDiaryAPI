package com.devumut.DearDiary.repositories;

import com.devumut.DearDiary.domain.entities.DiaryEntity;
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
}
