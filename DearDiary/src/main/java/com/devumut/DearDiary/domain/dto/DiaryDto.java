package com.devumut.DearDiary.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryDto {
    private UUID diary_id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date diary_date;
    private String diary_content;
    private int diary_emotion = -1;
}
