package com.devumut.DearDiary.repositories;

import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.domain.entities.UserEntity;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class DiaryEntityRepositoryIntegrationTest {

    @Autowired
    private DiaryRepository underTest;

    @Autowired
    private UserRepository userRepository;

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
    public void testThatUserCanGetAllDiaries() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();

        UserEntity savedUser = userRepository.save(userEntity);
        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();


        diaryEntity.setUser(savedUser);
        Optional<DiaryEntity> optionalSavedDiary = underTest.findById(underTest.save(diaryEntity).getDiary_id());
        assertThat(optionalSavedDiary).isPresent();
        DiaryEntity savedDiary = optionalSavedDiary.get();

        List<DiaryEntity> diaries = underTest.getAllDiaries(savedUser.getUser_id());

        assertThat(diaries.isEmpty()).isFalse();
        assertThat(diaries.get(0)).isEqualTo(savedDiary);
    }
}
