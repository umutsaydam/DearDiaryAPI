package com.devumut.DearDiary.controllers;

import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.dto.DiaryDto;
import com.devumut.DearDiary.domain.entities.DiaryEntity;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.exceptions.DiaryNotFoundException;
import com.devumut.DearDiary.jwt.JwtUtil;
import com.devumut.DearDiary.mappers.impl.DiaryMapper;
import com.devumut.DearDiary.services.DiaryService;
import com.devumut.DearDiary.services.EmotionPredictService;
import com.devumut.DearDiary.services.TokenService;
import com.devumut.DearDiary.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class DiaryControllerIntegrationTests {

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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DiaryMapper entityDtoMapper;  // MockMapper

    @MockitoBean
    private EmotionPredictService emotionPredictService;

    @BeforeEach
    public void setupMock() {
        when(emotionPredictService.predictEmotionFromText(Mockito.anyString()))
                .thenReturn(2);
    }

    /*
                            -- WARNING --
           please check the flaskUrl in EmotionPredictServiceImpl.
           be sure FlaskAPI is working on the host.
    */
    @Test
    public void testThatDiaryCanBeSavedSuccessfullyReturnsHttp200Ok() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        LocalDateTime localDateTime = LocalDateTime.of(2025, 4, 18, 7, 57);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        DiaryDto diaryDto = TestDataUtil.getDiaryDtoA();
        diaryDto.setDiary_date(date);
        diaryDto.setDiary_emotion(2);

        String diaryJson = mapper.writeValueAsString(diaryDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/diary/save-diary")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(diaryJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatDiaryCanBeSavedSuccessfullyReturnsSavedDiary() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);
        DiaryDto diaryDto = TestDataUtil.getDiaryDtoA();

        String diaryJson = mapper.writeValueAsString(diaryDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/diary/save-diary")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(diaryJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.diary_id").isNotEmpty()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.diary_date").isNotEmpty()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.diary_content").value(diaryDto.getDiary_content())
        );
    }

    @Test
    public void testThatDiaryCanGetAllDiariesSuccessfullyReturnsHttp200Ok() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        DiaryEntity diaryA = TestDataUtil.getDiaryEntityA();
        diaryA.setUser(userEntity);
        diaryA.setDiary_emotion(1);

        DiaryEntity diaryB = TestDataUtil.getDiaryEntityB();
        diaryB.setUser(userEntity);
        diaryB.setDiary_emotion(1);

        diaryService.saveDiary(diaryA);
        diaryService.saveDiary(diaryB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/diary/get-diaries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatDiaryCanGetAllDiariesSuccessfullyReturnsDiaries() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        DiaryEntity diaryA = TestDataUtil.getDiaryEntityA();
        diaryA.setUser(userEntity);
        diaryA.setDiary_emotion(1);

        DiaryEntity diaryB = TestDataUtil.getDiaryEntityB();
        diaryB.setUser(userEntity);
        diaryB.setDiary_emotion(1);

        DiaryEntity diaryEntityA = diaryService.saveDiary(diaryA);
        DiaryEntity diaryEntityB = diaryService.saveDiary(diaryB);

        Optional<DiaryEntity> optionalDiaryEntityA = diaryService.getDiaryById(diaryEntityA.getDiary_id());
        Optional<DiaryEntity> optionalDiaryEntityB = diaryService.getDiaryById(diaryEntityB.getDiary_id());
        assertThat(optionalDiaryEntityA).isPresent();
        diaryEntityA = optionalDiaryEntityA.get();
        assertThat(optionalDiaryEntityB).isPresent();
        diaryEntityB = optionalDiaryEntityB.get();

        List<DiaryDto> diaryDtoList = List.of(entityDtoMapper.mapTo(diaryEntityA), entityDtoMapper.mapTo(diaryEntityB));

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/diary/get-diaries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        String responseJsonResult = result.getResponse().getContentAsString();
        JsonNode jsonNodeResult = mapper.readTree(responseJsonResult);

        List<DiaryDto> diaryDtoListResult = new ArrayList<>();
        assertThat(jsonNodeResult.isArray()).isTrue();

        for (JsonNode diaryNode : jsonNodeResult) {
            DiaryDto dto = mapper.treeToValue(diaryNode, DiaryDto.class);
            diaryDtoListResult.add(dto);
        }

        assertThat(diaryDtoListResult).containsAll(diaryDtoList);
    }

    @Test
    public void testThatDiaryCanBeDeletedSuccessfullyReturnsHttp200Ok() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        DiaryEntity diaryA = TestDataUtil.getDiaryEntityA();
        diaryA.setUser(userEntity);
        diaryA.setDiary_emotion(1);
        DiaryEntity savedEntity = diaryService.saveDiary(diaryA);
        DiaryDto savedDto = entityDtoMapper.mapTo(savedEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/diary/delete-diary/" + savedDto.getDiary_id())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatDiaryCanBeUpdatedSuccessfullyReturnsHttp200Ok() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        DiaryEntity diaryA = TestDataUtil.getDiaryEntityA();
        diaryA.setUser(userEntity);
        diaryA.setDiary_emotion(1);
        DiaryEntity savedEntity = diaryService.saveDiary(diaryA);

        DiaryDto savedDto = entityDtoMapper.mapTo(savedEntity);
        savedDto.setDiary_content("I am sad.");

        String savedJson = mapper.writeValueAsString(savedDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/diary/update-diary")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(savedJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatDiaryCanBeUpdatedSuccessfullyReturnsUpdatedDiary() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        DiaryEntity diaryA = TestDataUtil.getDiaryEntityA();
        diaryA.setUser(userEntity);
        diaryA.setDiary_emotion(1);
        DiaryEntity savedEntity = diaryService.saveDiary(diaryA);

        DiaryDto savedDto = entityDtoMapper.mapTo(savedEntity);
        savedDto.setDiary_content("I am sad.");
        savedDto.setDiary_emotion(0);

        String savedJson = mapper.writeValueAsString(savedDto);


        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.patch("/diary/update-diary")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(savedJson)
        ).andReturn();

        Optional<DiaryEntity> optionalUpdatedDiary = diaryService.getDiaryById(savedDto.getDiary_id());
        assertThat(optionalUpdatedDiary).isPresent();
        DiaryEntity updatedDiary = optionalUpdatedDiary.get();
        DiaryDto updatedDto = entityDtoMapper.mapTo(updatedDiary);

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = mapper.readTree(jsonResponse);

        assertThat(jsonNode.get("diary_id").asText()).isEqualTo(updatedDto.getDiary_id().toString());
        assertThat(jsonNode.get("diary_content").asText()).isEqualTo(updatedDto.getDiary_content());
        assertThat(jsonNode.get("diary_emotion").asInt()).isEqualTo(updatedDto.getDiary_emotion());
    }

    @Test
    public void testThatCanNotUpdateDiaryCauseNotSavedReturnsDiaryNotFoundException() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity userEntity = userService.loginUser(TestDataUtil.getUserEntityA());

        String token = jwtUtil.generateToken(userEntity.getUser_id(), userEntity.getUsername());
        tokenService.saveToken(userEntity.getUser_id(), token);

        DiaryEntity diaryA = TestDataUtil.getDiaryEntityA();
        diaryA.setDiary_id(UUID.randomUUID());
        diaryA.setUser(userEntity);
        diaryA.setDiary_emotion(1);

        DiaryDto savedDto = entityDtoMapper.mapTo(diaryA);
        savedDto.setDiary_content("I am sad.");

        String savedJson = mapper.writeValueAsString(savedDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/diary/update-diary")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(savedJson)
        ).andExpect( mvcResult ->
                assertInstanceOf(DiaryNotFoundException.class, mvcResult.getResolvedException())
        );
    }
}
