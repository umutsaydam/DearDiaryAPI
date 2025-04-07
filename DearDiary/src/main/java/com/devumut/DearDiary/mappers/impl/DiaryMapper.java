package com.devumut.DearDiary.mappers.impl;

import com.devumut.DearDiary.domain.dto.DiaryDto;
import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiaryMapper implements Mapper<DiaryEntity, DiaryDto> {

    private final ModelMapper mapper;

    @Autowired
    public DiaryMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public DiaryDto mapTo(DiaryEntity diaryEntity) {
        return mapper.map(diaryEntity, DiaryDto.class);
    }

    @Override
    public DiaryEntity mapFrom(DiaryDto diaryDto) {
        return mapper.map(diaryDto, DiaryEntity.class);
    }
}
