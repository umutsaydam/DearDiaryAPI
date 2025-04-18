package com.devumut.DearDiary.domain.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryEmotionEntity {
    int emotion_id;
    int emotion_count;
}
