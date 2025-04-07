package com.devumut.DearDiary.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryDto {
    private UUID diary_id;
    private Date diary_date;
    private String diary_content;
    private int diary_emotion = 0;
}
