package com.devumut.DearDiary.repositories;

import com.devumut.DearDiary.domain.entities.DiaryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiaryRepository extends CrudRepository<DiaryEntity, UUID> {

    @Query("SELECT d FROM DiaryEntity d WHERE d.user.user_id = ?1")
    List<DiaryEntity> getAllDiaries(UUID userId);
}
