package com.devumut.DearDiary;

import com.devumut.DearDiary.domain.dto.UserDto;
import com.devumut.DearDiary.domain.entities.UserEntity;

public final class TestDataUtil {

    public TestDataUtil() {
    }

    public static UserEntity getUserEntityA() {
        return UserEntity
                .builder()
                .user_id(null)
                .username("umut")
                .password("password")
                .build();
    }

    public static UserDto getUserDtoA() {
        return UserDto
                .builder()
                .user_id(null)
                .username("umut")
                .password("password")
                .build();
    }
}
