package com.devumut.DearDiary.services;

import com.devumut.DearDiary.domain.entities.DiaryEntity;

import java.util.List;
import java.util.UUID;

public interface DiaryService {
    DiaryEntity saveDiary(DiaryEntity diary);

    void deleteDiary(UUID diaryId);

    DiaryEntity updateDiary(DiaryEntity diary);

    List<DiaryEntity> getAllDiariesByUserId(UUID userId);
}
