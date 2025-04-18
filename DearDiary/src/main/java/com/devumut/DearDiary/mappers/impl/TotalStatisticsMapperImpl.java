package com.devumut.DearDiary.mappers.impl;

import com.devumut.DearDiary.domain.dto.TotalStatisticsDto;
import com.devumut.DearDiary.domain.entities.TotalDiaryStatisticsEntity;
import com.devumut.DearDiary.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TotalStatisticsMapperImpl implements Mapper<TotalDiaryStatisticsEntity, TotalStatisticsDto> {

    private final ModelMapper mapper;

    @Autowired
    public TotalStatisticsMapperImpl(ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public TotalStatisticsDto mapTo(TotalDiaryStatisticsEntity totalDiaryStatisticsEntity) {
        return mapper.map(totalDiaryStatisticsEntity, TotalStatisticsDto.class);
    }

    @Override
    public TotalDiaryStatisticsEntity mapFrom(TotalStatisticsDto totalStatisticsDto) {
        return mapper.map(totalStatisticsDto, TotalDiaryStatisticsEntity.class);
    }
}
