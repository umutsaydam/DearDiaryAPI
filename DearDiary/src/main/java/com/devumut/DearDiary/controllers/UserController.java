package com.devumut.DearDiary.controllers;

import com.devumut.DearDiary.domain.dto.PasswordDto;
import com.devumut.DearDiary.domain.dto.UserDto;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.exceptions.PasswordsAreSameException;
import com.devumut.DearDiary.exceptions.PasswordsDoNotMatchException;
import com.devumut.DearDiary.exceptions.TokenNotValidException;
import com.devumut.DearDiary.exceptions.UsernameAlreadyTakenException;
import com.devumut.DearDiary.jwt.JwtUtil;
import com.devumut.DearDiary.mappers.Mapper;
import com.devumut.DearDiary.services.TokenService;
import com.devumut.DearDiary.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final Mapper<UserEntity, UserDto> mapper;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Autowired
    public UserController(Mapper<UserEntity, UserDto> mapper, UserService userService, JwtUtil jwtUtil, TokenService tokenService) {
        this.mapper = mapper;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        UserEntity userEntity = mapper.mapFrom(userDto);
        if (userService.isUserExistByUsername(userEntity.getUsername())) {
            throw new UsernameAlreadyTakenException("This username already taken!");
        }
        userService.createUser(userEntity);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto userDto) {
        UserEntity userEntity = mapper.mapFrom(userDto);
        UserEntity loginUser = userService.loginUser(userEntity);

        String token = jwtUtil.generateToken(loginUser.getUser_id(), loginUser.getUsername());
        tokenService.removeTokensByUserId(loginUser.getUser_id());
        tokenService.saveToken(loginUser.getUser_id(), token);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token) {
        token = jwtUtil.extractTokenFromHeader(token);
        if (!tokenService.isTokenValid(token)) {
            throw new TokenNotValidException("Token is not valid.");
        }
        tokenService.removeToken(token);
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody PasswordDto passwordDto
    ) {
        token = jwtUtil.extractTokenFromHeader(token);
        if (!tokenService.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!passwordDto.getNew_password().equals(passwordDto.getNew_password_confirm())) {
            throw new PasswordsDoNotMatchException("The new password and its confirmation do not match.");
        }
        if (passwordDto.getCurrent_password().equals(passwordDto.getNew_password())) {
            throw new PasswordsAreSameException("New password cannot be the same as the old password.");
        }

        UUID userId = jwtUtil.extractUserId(token);
        UserEntity user = userService.changePasswordByUserId(userId, passwordDto.getCurrent_password(), passwordDto.getNew_password());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
