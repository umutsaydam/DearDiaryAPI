package com.devumut.DearDiary;

import com.devumut.DearDiary.domain.dto.DiaryDto;
import com.devumut.DearDiary.domain.dto.UserDto;
import com.devumut.DearDiary.domain.entities.DiaryEntity;
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

    public static DiaryEntity getDiaryEntityA() {
        return DiaryEntity.
                builder()
                .diary_content("Diary ContentA")
                .diary_emotion(0)
                .build();
    }

    public static DiaryDto getDiaryDtoA() {
        return DiaryDto
                .builder()
                .diary_content("Diary ContentA")
                .diary_emotion(0)
                .build();
    }

    public static DiaryEntity getDiaryEntityB() {
        return DiaryEntity.
                builder()
                .diary_content("Diary ContentB")
                .diary_emotion(1)
                .build();
    }

    public static DiaryDto getDiaryDtoB() {
        return DiaryDto
                .builder()
                .diary_content("Diary ContentB")
                .diary_emotion(1)
                .build();
    }
}
