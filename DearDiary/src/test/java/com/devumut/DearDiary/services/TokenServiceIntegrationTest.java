package com.devumut.DearDiary.services;

import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class TokenServiceIntegrationTest {

    @Autowired
    private TokenService underTest;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Test
    public void testThatTokenCanBeSavedAndTokenIsValid() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        userService.createUser(userEntity);
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(savedUser.getUser_id(), savedUser.getUsername());
        underTest.saveToken(savedUser.getUser_id(), token);

        assertThat(underTest.isTokenValid(token)).isTrue();
    }

    @Test
    public void testThatTokenIsNotValid() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        userService.createUser(userEntity);
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(savedUser.getUser_id(), savedUser.getUsername());

        assertThat(underTest.isTokenValid(token)).isFalse();
    }

    @Test
    public void testThatTokenCanRemove() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        userService.createUser(userEntity);
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(savedUser.getUser_id(), savedUser.getUsername());
        underTest.saveToken(savedUser.getUser_id(), token);
        assertThat(underTest.isTokenValid(token)).isTrue();

        underTest.removeToken(token);
        assertThat(underTest.isTokenValid(token)).isFalse();
    }

    @Test
    public void testThatTokenCanRemoveByUserId() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        userService.createUser(userEntity);
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(savedUser.getUser_id(), savedUser.getUsername());
        underTest.saveToken(savedUser.getUser_id(), token);
        assertThat(underTest.isTokenValid(token)).isTrue();

        underTest.removeTokensByUserId(userEntity.getUser_id());
        assertThat(underTest.isTokenValid(token)).isFalse();
    }

}
