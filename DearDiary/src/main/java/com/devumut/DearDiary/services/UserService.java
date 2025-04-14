package com.devumut.DearDiary.services;

import com.devumut.DearDiary.domain.entities.UserEntity;

import java.util.UUID;

public interface UserService {
    void createUser(UserEntity user);

    boolean isUserExistByUsername(String username);

    UserEntity loginUser(UserEntity user);

    UserEntity changePasswordByUserId(UUID userId, String currentPassword, String newPassword);
}
