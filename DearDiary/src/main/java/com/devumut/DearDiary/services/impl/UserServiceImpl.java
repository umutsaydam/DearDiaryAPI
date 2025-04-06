package com.devumut.DearDiary.services.impl;

import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.repositories.UserRepository;
import com.devumut.DearDiary.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        if (isUserExistByUsername(user.getUsername())) {
            throw new RuntimeException("This username already taken!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    public boolean isUserExistByUsername(String username) {
        return repository.findByUsername(username).isPresent();
    }

    @Override
    public UserEntity loginUser(UserEntity user) {
        Optional<UserEntity> optionalUserEntity = repository.findByUsername(user.getUsername());

        if (optionalUserEntity.isPresent()) {
            UserEntity userEntity = optionalUserEntity.get();
            if (passwordEncoder.matches(user.getPassword(), userEntity.getPassword())) {
                return userEntity;
            }
        }
        throw new RuntimeException("Username or password wrong.");
    }

    @Override
    public UserEntity changePasswordByUserId(UUID userId, String currentPassword, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        return repository.findById(userId).map(existingUser -> {
            Optional.ofNullable(encodedPassword).ifPresent(existingUser::setPassword);
            return repository.save(existingUser);
        }).orElseThrow(() -> new RuntimeException("User does not exist."));
    }
}
