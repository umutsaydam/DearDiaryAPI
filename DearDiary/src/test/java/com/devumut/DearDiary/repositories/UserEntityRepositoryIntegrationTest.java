package com.devumut.DearDiary.repositories;

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
import com.devumut.DearDiary.TestDataUtil;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserEntityRepositoryIntegrationTest {

    @Autowired
    private UserRepository underTest;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
