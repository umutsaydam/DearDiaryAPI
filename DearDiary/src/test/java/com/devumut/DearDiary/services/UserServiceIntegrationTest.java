package com.devumut.DearDiary.services;

import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService underTest;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
