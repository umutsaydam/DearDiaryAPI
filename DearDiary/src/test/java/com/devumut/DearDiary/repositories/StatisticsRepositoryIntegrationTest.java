package com.devumut.DearDiary.repositories;

import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class StatisticsRepositoryIntegrationTest {

    @Autowired
    private StatisticsRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiaryRepository diaryRepository;

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
        diaryEntity.setUser(savedUser);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime localDateTime = LocalDateTime.parse("2025-04-18T07:57");
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryRepository.save(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(savedUser);
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
        diaryEntity.setUser(savedUser);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime localDateTime = LocalDateTime.parse("2025-04-18T07:57");
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryRepository.save(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(savedUser);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-09T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryRepository.save(diaryEntityB);

        List<Object[]> result = underTest.getTotalEmotions(savedUser.getUser_id(), "last_week");

        assertThat(!result.isEmpty()).isTrue();
        assertThat(result.size()).isEqualTo(1);
        assertThat(((Number) result.get(0)[0]).intValue()).isEqualTo(1);
        assertThat(((Number) result.get(0)[1]).intValue()).isEqualTo(1);
    }

    @Test
    public void testThatCanGetTotalStatisticsThisMont() {
        UserEntity userEntity = TestDataUtil.getUserEntityA();
        UserEntity savedUser = userRepository.save(userEntity);
        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(savedUser);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime localDateTime = LocalDateTime.parse("2025-04-18T07:57");
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryRepository.save(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(savedUser);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-01T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryRepository.save(diaryEntityB);

        List<Object[]> result = underTest.getTotalEmotions(savedUser.getUser_id(), "this_month");

        assertThat(!result.isEmpty()).isTrue();
        assertThat(result.size()).isEqualTo(2);
        assertThat(((Number) result.get(0)[0]).intValue()).isEqualTo(0);
        assertThat(((Number) result.get(0)[1]).intValue()).isEqualTo(1);
        assertThat(((Number) result.get(1)[0]).intValue()).isEqualTo(1);
        assertThat(((Number) result.get(1)[1]).intValue()).isEqualTo(1);
    }
}
