package com.devumut.DearDiary.services;


import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.entities.DiaryEmotionEntity;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class StatisticsServiceIntegrationTest {

    @Autowired
    private StatisticsService underTest;

    @Autowired
    private UserService userService;

    @Autowired
    private DiaryService diaryService;

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
    public void testThatCanGetTotalEmotionCountsWithNoData() {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        List<DiaryEmotionEntity> list = underTest.getTotalEmotionCounts(savedUser.getUser_id(), "all");
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    public void testThatCanGetTotalEmotionWithDataAll() {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(savedUser);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime localDateTime = LocalDateTime.parse("2025-04-18T07:57");
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryService.saveDiary(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(savedUser);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-01T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryService.saveDiary(diaryEntityB);

        List<DiaryEmotionEntity> list = underTest.getTotalEmotionCounts(savedUser.getUser_id(), "all");
        assertThat(!list.isEmpty()).isTrue();
        assertThat(list.get(0).getEmotion_id()).isEqualTo(0);
        assertThat(list.get(0).getEmotion_count()).isEqualTo(1);
        assertThat(list.get(1).getEmotion_id()).isEqualTo(1);
        assertThat(list.get(1).getEmotion_count()).isEqualTo(1);
    }

    @Test
    public void testThatCanGetTotalEmotionWithDataThisWeek() {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(savedUser);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryService.saveDiary(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(savedUser);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-01T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryService.saveDiary(diaryEntityB);

        List<DiaryEmotionEntity> list = underTest.getTotalEmotionCounts(savedUser.getUser_id(), "this_week");
        assertThat(!list.isEmpty()).isTrue();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getEmotion_id()).isEqualTo(0);
        assertThat(list.get(0).getEmotion_count()).isEqualTo(1);
    }

    @Test
    public void testThatCanGetTotalEmotionWithDataLastWeek() {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(savedUser);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        Date date = Date.from(oneWeekAgo.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryService.saveDiary(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(savedUser);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-10T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryService.saveDiary(diaryEntityB);

        List<DiaryEmotionEntity> list = underTest.getTotalEmotionCounts(savedUser.getUser_id(), "last_week");
        assertThat(!list.isEmpty()).isTrue();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getEmotion_id()).isEqualTo(0);
        assertThat(list.get(0).getEmotion_count()).isEqualTo(1);
    }

    @Test
    public void testThatCanGetTotalEmotionWithDataThisMonth() {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(savedUser);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime localDateTime = LocalDateTime.parse("2025-04-18T07:57");
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryService.saveDiary(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(savedUser);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-10T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryService.saveDiary(diaryEntityB);

        List<DiaryEmotionEntity> list = underTest.getTotalEmotionCounts(savedUser.getUser_id(), "this_month");
        assertThat(!list.isEmpty()).isTrue();
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0).getEmotion_id()).isEqualTo(0);
        assertThat(list.get(0).getEmotion_count()).isEqualTo(1);
        assertThat(list.get(1).getEmotion_id()).isEqualTo(1);
        assertThat(list.get(1).getEmotion_count()).isEqualTo(1);
    }
}
