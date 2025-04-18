package com.devumut.DearDiary.controllers;

import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.dto.DiaryDto;
import com.devumut.DearDiary.domain.dto.DiaryEmotionDto;
import com.devumut.DearDiary.domain.dto.TotalStatisticsDto;
import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.jwt.JwtUtil;
import com.devumut.DearDiary.mappers.Mapper;
import com.devumut.DearDiary.services.DiaryService;
import com.devumut.DearDiary.services.StatisticsService;
import com.devumut.DearDiary.services.TokenService;
import com.devumut.DearDiary.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class StatisticsControllerIntegrationTests {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private StatisticsService underTest;

    @Autowired
    private Mapper<DiaryEntity, DiaryDto> entityDtoMapper;

    @Test
    public void testThatTotalDiaryStatisticsReturnsHttp200Ok() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/statistics/total-diary-statistics")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatTotalDiaryStatisticsReturnsTotalDiaryDto() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/statistics/total-diary-statistics")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.totalDiaries").value(0)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.currentStreak").value(0)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.longestStreak").value(0)
        );
    }

    @Test
    public void testThatTotalDiaryStatisticsReturnsTotalDiaryDtoWithData() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(userEntity);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime localDateTime = LocalDateTime.parse("2025-04-18T07:57");
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryService.saveDiary(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(userEntity);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-01T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryService.saveDiary(diaryEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/statistics/total-diary-statistics")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.totalDiaries").value(2)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.currentStreak").value(1)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.longestStreak").value(1)
        );
    }

    @Test
    public void testThatTotalEmotionStatisticsReturnsHttp200Ok() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/statistics/total-emotion-statistics")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatTotalEmotionStatisticsReturnsDiaryEmotionDtoWithAll() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(userEntity);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime localDateTime = LocalDateTime.parse("2025-04-18T07:57");
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryService.saveDiary(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(userEntity);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-01T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryService.saveDiary(diaryEntityB);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/statistics/total-emotion-statistics?timeRange=all")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        String responseJsonResult = result.getResponse().getContentAsString();
        JsonNode jsonNodeResult = mapper.readTree(responseJsonResult);

        List<DiaryEmotionDto> diaryEmotionDtoResult = new ArrayList<>();
        assertThat(jsonNodeResult.isArray()).isTrue();

        for (JsonNode emotionNode : jsonNodeResult) {
            DiaryEmotionDto dto = mapper.treeToValue(emotionNode, DiaryEmotionDto.class);
            diaryEmotionDtoResult.add(dto);
        }

        assertThat(diaryEmotionDtoResult).isNotEmpty();
        assertThat(diaryEmotionDtoResult.size()).isEqualTo(2);
        assertThat(diaryEmotionDtoResult.get(0).getEmotion_id()).isEqualTo(0);
        assertThat(diaryEmotionDtoResult.get(0).getEmotion_count()).isEqualTo(1);
        assertThat(diaryEmotionDtoResult.get(1).getEmotion_id()).isEqualTo(1);
        assertThat(diaryEmotionDtoResult.get(1).getEmotion_count()).isEqualTo(1);
    }

    @Test
    public void testThatTotalEmotionStatisticsReturnsDiaryEmotionDtoWithThisWeek() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(userEntity);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime localDateTime = LocalDateTime.parse("2025-04-18T07:57");
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryService.saveDiary(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(userEntity);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-01T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryService.saveDiary(diaryEntityB);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/statistics/total-emotion-statistics?timeRange=this_week")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        String responseJsonResult = result.getResponse().getContentAsString();
        JsonNode jsonNodeResult = mapper.readTree(responseJsonResult);

        List<DiaryEmotionDto> diaryEmotionDtoResult = new ArrayList<>();
        assertThat(jsonNodeResult.isArray()).isTrue();

        for (JsonNode emotionNode : jsonNodeResult) {
            DiaryEmotionDto dto = mapper.treeToValue(emotionNode, DiaryEmotionDto.class);
            diaryEmotionDtoResult.add(dto);
        }

        assertThat(diaryEmotionDtoResult).isNotEmpty();
        assertThat(diaryEmotionDtoResult.size()).isEqualTo(1);
        assertThat(diaryEmotionDtoResult.get(0).getEmotion_id()).isEqualTo(0);
        assertThat(diaryEmotionDtoResult.get(0).getEmotion_count()).isEqualTo(1);
    }

    @Test
    public void testThatTotalEmotionStatisticsReturnsDiaryEmotionDtoWithLastWeek() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(userEntity);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime localDateTime = LocalDateTime.parse("2025-04-18T07:57");
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryService.saveDiary(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(userEntity);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-10T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryService.saveDiary(diaryEntityB);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/statistics/total-emotion-statistics?timeRange=last_week")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        String responseJsonResult = result.getResponse().getContentAsString();
        JsonNode jsonNodeResult = mapper.readTree(responseJsonResult);

        List<DiaryEmotionDto> diaryEmotionDtoResult = new ArrayList<>();
        assertThat(jsonNodeResult.isArray()).isTrue();

        for (JsonNode emotionNode : jsonNodeResult) {
            DiaryEmotionDto dto = mapper.treeToValue(emotionNode, DiaryEmotionDto.class);
            diaryEmotionDtoResult.add(dto);
        }

        assertThat(diaryEmotionDtoResult).isNotEmpty();
        assertThat(diaryEmotionDtoResult.size()).isEqualTo(1);
        assertThat(diaryEmotionDtoResult.get(0).getEmotion_id()).isEqualTo(1);
        assertThat(diaryEmotionDtoResult.get(0).getEmotion_count()).isEqualTo(1);
    }

    @Test
    public void testThatTotalEmotionStatisticsReturnsDiaryEmotionDtoWithThisMonth() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        DiaryEntity diaryEntity = TestDataUtil.getDiaryEntityA();
        diaryEntity.setUser(userEntity);
        diaryEntity.setDiary_emotion(0);
        LocalDateTime localDateTime = LocalDateTime.parse("2025-04-18T07:57");
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntity.setDiary_date(date);
        diaryService.saveDiary(diaryEntity);

        DiaryEntity diaryEntityB = TestDataUtil.getDiaryEntityB();
        diaryEntityB.setUser(userEntity);
        diaryEntityB.setDiary_emotion(1);
        LocalDateTime localDateTimeB = LocalDateTime.parse("2025-04-10T07:57");
        Date dateB = Date.from(localDateTimeB.atZone(ZoneId.systemDefault()).toInstant());
        diaryEntityB.setDiary_date(dateB);
        diaryService.saveDiary(diaryEntityB);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/statistics/total-emotion-statistics?timeRange=this_month")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        String responseJsonResult = result.getResponse().getContentAsString();
        JsonNode jsonNodeResult = mapper.readTree(responseJsonResult);

        List<DiaryEmotionDto> diaryEmotionDtoResult = new ArrayList<>();
        assertThat(jsonNodeResult.isArray()).isTrue();

        for (JsonNode emotionNode : jsonNodeResult) {
            DiaryEmotionDto dto = mapper.treeToValue(emotionNode, DiaryEmotionDto.class);
            diaryEmotionDtoResult.add(dto);
        }

        assertThat(diaryEmotionDtoResult).isNotEmpty();
        assertThat(diaryEmotionDtoResult.size()).isEqualTo(2);
        assertThat(diaryEmotionDtoResult.get(0).getEmotion_id()).isEqualTo(0);
        assertThat(diaryEmotionDtoResult.get(0).getEmotion_count()).isEqualTo(1);
        assertThat(diaryEmotionDtoResult.get(1).getEmotion_id()).isEqualTo(1);
        assertThat(diaryEmotionDtoResult.get(1).getEmotion_count()).isEqualTo(1);
    }
}
