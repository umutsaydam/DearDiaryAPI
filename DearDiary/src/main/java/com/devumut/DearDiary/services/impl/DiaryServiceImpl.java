package com.devumut.DearDiary.services.impl;

import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.repositories.DiaryRepository;
import com.devumut.DearDiary.services.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepository;

    @Autowired
    public DiaryServiceImpl(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    @Override
    public DiaryEntity saveDiary(DiaryEntity diary) {
        return diaryRepository.save(diary);
    }

    @Override
    public void deleteDiary(UUID diaryId) {
        diaryRepository.deleteById(diaryId);
    }

    @Override
    public DiaryEntity updateDiary(DiaryEntity diary) {
        return diaryRepository.findById(diary.getDiary_id()).map(existingDiary -> {
            Optional.ofNullable(diary.getDiary_date()).ifPresent(existingDiary::setDiary_date);
            Optional.ofNullable(diary.getDiary_content()).ifPresent(existingDiary::setDiary_content);
            Optional.ofNullable(diary.getDiary_emotion()).ifPresent(existingDiary::setDiary_emotion);
            return diaryRepository.save(existingDiary);
        }).orElseThrow(() -> new RuntimeException("Unknown a diary."));
    }

    @Override
    public List<DiaryEntity> getAllDiariesByUserId(UUID userId) {
        return diaryRepository.getAllDiaries(userId);
    }
}
