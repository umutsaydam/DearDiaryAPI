package com.devumut.DearDiary.mappers.impl;

import com.devumut.DearDiary.domain.dto.UserDto;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements Mapper<UserEntity, UserDto> {

    private final ModelMapper mapper;

    @Autowired
    public UserMapperImpl(ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public UserDto mapTo(UserEntity userEntity) {
        return mapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) {
        return mapper.map(userDto, UserEntity.class);
    }
}
