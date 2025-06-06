package com.devumut.DearDiary.services.impl;

import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.exceptions.DatabaseOperationException;
import com.devumut.DearDiary.exceptions.DiaryNotFoundException;
import com.devumut.DearDiary.repositories.DiaryRepository;
import com.devumut.DearDiary.services.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepository;

    @Autowired
    public DiaryServiceImpl(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    @Override
    public DiaryEntity saveDiary(DiaryEntity diary) {
        try {
            return diaryRepository.save(diary);
        } catch (DataIntegrityViolationException ex) {
            throw new DatabaseOperationException("Failed to save diary", ex);
        }
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
        }).orElseThrow(() -> new DiaryNotFoundException("Unknown a diary."));
    }

    @Override
    public List<DiaryEntity> getAllDiariesByUserId(UUID userId) {
        return diaryRepository.getAllDiaries(userId);
    }

    @Override
    public Optional<DiaryEntity> getDiaryById(UUID diaryId) {
        return Optional.ofNullable(diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNotFoundException("Diary not found with the provided ID.")));
    }

    @Override
    public boolean isExistDiaryByDate(Date date) {
        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return diaryRepository.getDiaryByDate(localDate).isPresent();
    }
}
