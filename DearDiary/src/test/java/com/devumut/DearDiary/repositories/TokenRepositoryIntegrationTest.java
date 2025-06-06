package com.devumut.DearDiary.repositories;

import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.entities.TokenEntity;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.jwt.JwtUtil;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TokenRepositoryIntegrationTest {

    @Autowired
    private TokenRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

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
    public void testThatUserTokenCanGenerateAndSaved() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        UserEntity savedUser = userRepository.save(userEntity);

        String token = jwtUtil.generateToken(savedUser.getUser_id(), savedUser.getUsername());
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUser_id(savedUser.getUser_id());

        TokenEntity savedToken = underTest.save(tokenEntity);

        assertThat(savedToken.getId()).isNotNull();
        assertThat(savedToken.getUser_id()).isEqualTo(tokenEntity.getUser_id());
        assertThat(savedToken.getToken()).isEqualTo(tokenEntity.getToken());
    }

    @Test
    public void testThatUserTokenIsExisted() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        UserEntity savedUser = userRepository.save(userEntity);

        String token = jwtUtil.generateToken(savedUser.getUser_id(), savedUser.getUsername());
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUser_id(savedUser.getUser_id());
        TokenEntity savedToken = underTest.save(tokenEntity);

        Optional<TokenEntity> optionalTokenEntity = underTest.existByToken(savedToken.getToken());
        assertThat(optionalTokenEntity).isPresent();
        TokenEntity tokenEntity1 = optionalTokenEntity.get();
        assertThat(tokenEntity1.getToken()).isEqualTo(savedToken.getToken());
    }

    @Test
    public void testThatUserTokenCanBeDeletedByToken() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        UserEntity savedUser = userRepository.save(userEntity);

        String token = jwtUtil.generateToken(savedUser.getUser_id(), savedUser.getUsername());
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUser_id(savedUser.getUser_id());
        TokenEntity savedToken = underTest.save(tokenEntity);

        underTest.deleteByToken(savedToken.getToken());

        Optional<TokenEntity> optionalTokenEntity = underTest.existByToken(savedToken.getToken());
        assertThat(optionalTokenEntity).isNotPresent();
    }

    @Test
    public void testThatUserTokenCanBeDeletedByUserId() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        UserEntity savedUser = userRepository.save(userEntity);

        String token = jwtUtil.generateToken(savedUser.getUser_id(), savedUser.getUsername());
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUser_id(savedUser.getUser_id());
        TokenEntity savedToken = underTest.save(tokenEntity);

        underTest.deleteTokensByUserId(savedToken.getUser_id());

        Optional<TokenEntity> optionalTokenEntity = underTest.existByToken(savedToken.getToken());
        assertThat(optionalTokenEntity).isNotPresent();
    }
}
