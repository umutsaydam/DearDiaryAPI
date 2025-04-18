package com.devumut.DearDiary.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryEmotionDto {
    int emotion_id;
    int emotion_count;
}
