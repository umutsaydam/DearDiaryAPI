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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class DiaryEntityRepositoryIntegrationTest {

    @Autowired
    private DiaryRepository underTest;

    @Autowired
    private UserRepository userRepository;

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
