package com.devumut.DearDiary.services;

import java.util.UUID;

public interface TokenService {
    void saveToken(UUID userId, String token);

    boolean isTokenValid(String token);

    void removeToken(String token);

    void removeTokensByUserId(UUID userId);
}
