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
public class StatisticsRepositoryIntegrationTest {

    @Autowired
    private StatisticsRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiaryRepository diaryRepository;

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
    public void testThatCanGetTotalStatisticsAll() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        UserEntity savedUser = userRepository.save(userEntity);
        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(savedUser);
        diaryEntity.setDiary_emotion(0);
        diaryRepository.save(diaryEntity);

        List<Object[]> result = underTest.getTotalEmotions(savedUser.getUser_id(), "all");

        assertThat(!result.isEmpty()).isTrue();
        assertThat(result.size()).isEqualTo(1);
        assertThat(((Number) result.get(0)[0]).intValue()).isEqualTo(0);
        assertThat(((Number) result.get(0)[1]).intValue()).isEqualTo(1);
    }

    @Test
    public void testThatCanGetTotalStatisticsThisWeek() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        UserEntity savedUser = userRepository.save(userEntity);
        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(userEntity);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryRepository.save(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(userEntity);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-01T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryRepository.save(diaryEntityB);

        List<Object[]> result = underTest.getTotalEmotions(savedUser.getUser_id(), "this_week");

        assertThat(!result.isEmpty()).isTrue();
        assertThat(result.size()).isEqualTo(1);
        assertThat(((Number) result.get(0)[0]).intValue()).isEqualTo(0);
        assertThat(((Number) result.get(0)[1]).intValue()).isEqualTo(1);
    }

    @Test
    public void testThatCanGetTotalStatisticsLastWeek() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        UserEntity savedUser = userRepository.save(userEntity);
        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(userEntity);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        Date date = Date.from(oneWeekAgo.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryRepository.save(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(userEntity);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-10T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryRepository.save(diaryEntityB);

        List<Object[]> result = underTest.getTotalEmotions(savedUser.getUser_id(), "last_week");

        assertThat(!result.isEmpty()).isTrue();
        assertThat(result.size()).isEqualTo(1);
        assertThat(((Number) result.get(0)[0]).intValue()).isEqualTo(0);
        assertThat(((Number) result.get(0)[1]).intValue()).isEqualTo(1);
    }

    @Test
    public void testThatCanGetTotalStatisticsThisMont() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        UserEntity savedUser = userRepository.save(userEntity);
        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(userEntity);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryRepository.save(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(userEntity);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-03-10T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryRepository.save(diaryEntityB);

        List<Object[]> result = underTest.getTotalEmotions(savedUser.getUser_id(), "this_month");

        assertThat(!result.isEmpty()).isTrue();
        assertThat(result.size()).isEqualTo(1);
        assertThat(((Number) result.get(0)[0]).intValue()).isEqualTo(0);
        assertThat(((Number) result.get(0)[1]).intValue()).isEqualTo(1);
    }
}
