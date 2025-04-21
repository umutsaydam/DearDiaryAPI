package com.devumut.DearDiary.services;

import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.exceptions.DatabaseOperationException;
import com.devumut.DearDiary.exceptions.DiaryNotFoundException;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class DiaryServiceIntegrationTest {

    @Autowired
    private DiaryService underTest;

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
    public void testThatUsersDiaryCanBeSaved() {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(savedUser);
        DiaryEntity savedDiary = underTest.saveDiary(diaryEntity);

        assertThat(savedDiary).isEqualTo(diaryEntity);
    }

    @Test
    public void testThatUsersDiaryCanNotBeSavedDatabaseOperationException() {
        UserEntity notSavedUser = TestDataUtil.getUserEntityA();
        notSavedUser.setUser_id(UUID.randomUUID());
        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(notSavedUser);

        assertThrows(DatabaseOperationException.class, () -> {
            underTest.saveDiary(diaryEntity);
        });
    }

    @Test
    public void testThatUsersDiaryCanBeDeleted() {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(savedUser);
        DiaryEntity savedDiary = underTest.saveDiary(diaryEntity);

        underTest.deleteDiary(savedDiary.getDiary_id());
    }

    @Test
    public void testThatUserCanUpdateDiary() {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(savedUser);
        DiaryEntity savedDiary = underTest.saveDiary(diaryEntity);

        DiaryEntity diary = TestDataUtil.getDiaryEntityB();
        diary.setDiary_id(savedDiary.getDiary_id());
        diary.setUser(savedUser);

        DiaryEntity updatedDiary = underTest.updateDiary(diary);
        diary.setDiary_date(updatedDiary.getDiary_date());

        assertThat(updatedDiary).isEqualTo(diary);
    }

    @Test
    public void testThatUserCanNotUpdateDiaryWithNotSavedDiaryDiaryNotFoundException() {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setDiary_id(UUID.randomUUID());
        diaryEntity.setUser(savedUser);

        assertThrows(DiaryNotFoundException.class, () -> {
            underTest.updateDiary(diaryEntity);
        });
    }

    @Test
    public void testThatUserCanGetAllDiaries() {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        DiaryEntity diaryEntity1 = TestDataUtil.getDiaryEntityA();
        diaryEntity1.setUser(savedUser);
        underTest.saveDiary(diaryEntity1);
        DiaryEntity diaryEntity2 = TestDataUtil.getDiaryEntityB();
        diaryEntity2.setUser(savedUser);
        underTest.saveDiary(diaryEntity2);

        Optional<DiaryEntity> savedDiary1 = underTest.getDiaryById(diaryEntity1.getDiary_id());
        Optional<DiaryEntity> savedDiary2 = underTest.getDiaryById(diaryEntity2.getDiary_id());
        List<DiaryEntity> diaryEntityList = underTest.getAllDiariesByUserId(savedUser.getUser_id());

        List<DiaryEntity> savedList = List.of(savedDiary1.get(), savedDiary2.get());
        assertThat(diaryEntityList).isNotEmpty();
        assertThat(diaryEntityList).containsAll(savedList);
    }

    @Test
    public void testThatUserCanNotGetDiaryByDiaryIdDiaryNotFoundException() {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(savedUser);
        diaryEntity.setDiary_id(UUID.randomUUID());

        assertThrows(DiaryNotFoundException.class, () -> {
            underTest.getDiaryById(diaryEntity.getDiary_id());
        });
    }
}
