package com.devumut.DearDiary.services;

import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TokenServiceIntegrationTest {

    @Autowired
    private TokenService underTest;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

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
