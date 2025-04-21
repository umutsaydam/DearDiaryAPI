package com.devumut.DearDiary.services;

import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

    @Autowired
    private UserService underTest;

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
    public void testThatUserCanBeSaved() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();

        underTest.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = underTest.loginUser(TestDataUtil.getUserEntityA());

        assertThat(savedUser.getUser_id()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(userEntity.getUsername());
        assertThat(passwordEncoder.matches(userEntity.getPassword(), savedUser.getPassword())).isTrue();
    }

    @Test
    public void testThatUserCanNotBeSavedWithSameUsername() {
        underTest.createUser(TestDataUtil.getUserEntityA());

        assertThrows(RuntimeException.class, () -> underTest.createUser(TestDataUtil.getUserEntityA()));
    }

    @Test
    public void testThatExistUsernameForSavedUser() {
        underTest.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = underTest.loginUser(TestDataUtil.getUserEntityA());

        assertThat(underTest.isUserExistByUsername(savedUser.getUsername())).isTrue();
    }

    @Test
    public void testThatUserCanLoginSuccessfully() {
        underTest.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = underTest.loginUser(TestDataUtil.getUserEntityA());

        UserEntity loginUser = underTest.loginUser(TestDataUtil.getUserEntityA());

        assertThat(savedUser).isEqualTo(loginUser);
    }

    @Test
    public void testThatNotSavedUserCanNotLoginRuntimeException() {
        assertThrows(RuntimeException.class, () -> underTest.loginUser(TestDataUtil.getUserEntityA()));
    }

    @Test
    public void testThatUserCanChangePasswordByUserId() {
        underTest.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = underTest.loginUser(TestDataUtil.getUserEntityA());

        UserEntity updatedUser = underTest.changePasswordByUserId(savedUser.getUser_id(), "NEW_PASSWORD", "NEW_PASSWORD");

        assertThat(savedUser.getPassword()).isNotEqualTo(updatedUser.getPassword());
    }

    @Test
    public void testThatNotSavedUserCanNotChangePasswordByUserIdRuntimeException() {
        UserEntity notSavedUser = TestDataUtil.getUserEntityA();
        notSavedUser.setUser_id(UUID.randomUUID());

        assertThrows(RuntimeException.class, () -> underTest.changePasswordByUserId(notSavedUser.getUser_id(), "NEW_PASSWORD", "NEW_PASSWORD"));
    }
}
