package com.devumut.DearDiary.controllers;

import com.devumut.DearDiary.domain.dto.PasswordDto;
import com.devumut.DearDiary.domain.dto.UserDto;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.jwt.JwtUtil;
import com.devumut.DearDiary.mappers.Mapper;
import com.devumut.DearDiary.services.TokenService;
import com.devumut.DearDiary.services.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Log
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

        try {
            UserEntity savedUserEntity = userService.createUser(userEntity);
            UserDto savedUserDto = mapper.mapTo(savedUserEntity);

            return new ResponseEntity<>(savedUserDto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto userDto) {
        UserEntity userEntity = mapper.mapFrom(userDto);

        try {
            UserEntity loginUser = userService.loginUser(userEntity);
            String token = jwtUtil.generateToken(loginUser.getUser_id(), loginUser.getUsername());
            tokenService.removeTokensByUserId(loginUser.getUser_id());
            log.info(loginUser.getUser_id() + " */*/*/* " + token);
            tokenService.saveToken(loginUser.getUser_id(), token);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer")) {
            token = token.substring(7);
        }
        if (!tokenService.isTokenValid(token)) {
            return new ResponseEntity<>("Invalid token", HttpStatus.BAD_REQUEST);
        }
        tokenService.removeToken(token);
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody PasswordDto passwordDto
    ) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (!tokenService.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!passwordDto.getNew_password().equals(passwordDto.getNew_password_confirm()) || passwordDto.getCurrent_password().equals(passwordDto.getNew_password())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            UUID userId = jwtUtil.extractUserId(token);
            UserEntity user = userService.changePasswordByUserId(userId, passwordDto.getCurrent_password(), passwordDto.getNew_password());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
