package com.devumut.DearDiary.repositories;

import com.devumut.DearDiary.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import com.devumut.DearDiary.TestDataUtil;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserEntityRepositoryIntegrationTest {

    @Autowired
    private UserRepository underTest;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public void testThatUserCanBeRegister() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        UserEntity savedUser = underTest.save(userEntity);

        Optional<UserEntity> result = underTest.findById(savedUser.getUser_id());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(savedUser);
    }

    @Test
    public void testThatUserCanLogin(){
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        UserEntity savedUser = underTest.save(userEntity);

        UserEntity loginUser = TestDataUtil.getUserEntityA();

        Optional<UserEntity> optionalUserEntity = underTest.findByUsername(loginUser.getUsername());
        assertThat(optionalUserEntity).isPresent();
        UserEntity user = optionalUserEntity.get();
        assertThat(passwordEncoder.matches(loginUser.getPassword(), savedUser.getPassword())).isTrue();
    }
}
