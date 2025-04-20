package com.devumut.DearDiary.services;


import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.entities.DiaryEmotionEntity;
import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.domain.entities.TotalDiaryStatisticsEntity;
import com.devumut.DearDiary.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class StatisticsServiceIntegrationTest {

    @Autowired
    private StatisticsService underTest;

    @Autowired
    private UserService userService;

    @Autowired
    private DiaryService diaryService;

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

        List<DiaryEmotionEntity> list = underTest.getTotalEmotionCounts(savedUser.getUser_id(), "last_week");
        assertThat(!list.isEmpty()).isTrue();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getEmotion_id()).isEqualTo(1);
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
