package com.devumut.DearDiary.services.impl;

import com.devumut.DearDiary.domain.entities.TokenEntity;
import com.devumut.DearDiary.repositories.TokenRepository;
import com.devumut.DearDiary.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository repository;

    @Autowired
    public TokenServiceImpl(TokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveToken(UUID userId, String token) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUser_id(userId);
        tokenEntity.setToken(token);
        repository.save(tokenEntity);
    }

    @Override
    public boolean isTokenValid(String token) {
        return repository.existByToken(token).isPresent();
    }

    @Override
    public void removeToken(String token) {
        repository.deleteByToken(token);
    }

    @Override
    public void removeTokensByUserId(UUID userId) {
        repository.deleteTokensByUserId(userId);
    }
}
