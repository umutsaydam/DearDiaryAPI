package com.devumut.DearDiary.controllers;

import com.devumut.DearDiary.TestDataUtil;
import com.devumut.DearDiary.domain.dto.PasswordDto;
import com.devumut.DearDiary.domain.dto.UserDto;
import com.devumut.DearDiary.domain.entities.UserEntity;
import com.devumut.DearDiary.exceptions.PasswordsAreSameException;
import com.devumut.DearDiary.exceptions.PasswordsDoNotMatchException;
import com.devumut.DearDiary.exceptions.TokenNotValidException;
import com.devumut.DearDiary.jwt.JwtUtil;
import com.devumut.DearDiary.mappers.Mapper;
import com.devumut.DearDiary.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private Mapper<UserEntity, UserDto> entityDtoMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testThatUserRegisterSuccessfullyReturns201Created() throws Exception {
        UserEntity userEntity = TestDataUtil.getUserEntityA();

        String userJson = mapper.writeValueAsString(userEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }


    @Test
    public void testThatUserCanLoginSuccessfullyReturns200Ok() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());

        UserDto userDto = entityDtoMapper.mapTo(TestDataUtil.getUserEntityA());
        String userJson = mapper.writeValueAsString(userDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatUserCanLoginSuccessfullyReturnsUserToken() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());

        UserDto userDto = entityDtoMapper.mapTo(TestDataUtil.getUserEntityA());
        String userJson = mapper.writeValueAsString(userDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.token").isNotEmpty()
        );
    }

    @Test
    public void testThatUserCanLogoutSuccessfullyReturns200Ok() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());

        UserDto userDto = entityDtoMapper.mapTo(TestDataUtil.getUserEntityA());
        String userJson = mapper.writeValueAsString(userDto);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andReturn();

        String responseJson = result.getResponse().getContentAsString();
        JsonNode jsonNode = mapper.readTree(responseJson);
        String token = jsonNode.get("token").asText();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatUserCanNotLogoutReturnsTokenNotValidException() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());
        UserEntity savedUser = userService.loginUser(TestDataUtil.getUserEntityA());

        String invalidToken = jwtUtil.generateToken(savedUser.getUser_id(), savedUser.getUsername());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + invalidToken)
        ).andExpect(mvcResult ->
                assertInstanceOf(TokenNotValidException.class, mvcResult.getResolvedException())
        );
    }

    @Test
    public void testThatUserCanChangePasswordSuccessfullyReturns200Ok() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());

        UserDto userDto = entityDtoMapper.mapTo(TestDataUtil.getUserEntityA());
        String userJson = mapper.writeValueAsString(userDto);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andReturn();

        String responseJson = result.getResponse().getContentAsString();
        JsonNode jsonNode = mapper.readTree(responseJson);
        String token = jsonNode.get("token").asText();

        String newPassword = "NEW_PASSWORD";
        String newPasswordConfirm = "NEW_PASSWORD";
        PasswordDto passwordDto = new PasswordDto(userDto.getPassword(), newPassword, newPasswordConfirm);
        String passwordJson = mapper.writeValueAsString(passwordDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(passwordJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatUserCanNotChangePasswordReturnsPasswordsDoNotMatchException() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());

        UserDto userDto = entityDtoMapper.mapTo(TestDataUtil.getUserEntityA());
        String userJson = mapper.writeValueAsString(userDto);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andReturn();

        String responseJson = result.getResponse().getContentAsString();
        JsonNode jsonNode = mapper.readTree(responseJson);
        String token = jsonNode.get("token").asText();

        String newPassword = "NEW_PASSWORD";
        String newPasswordConfirm = "NEW_PASSWORD_TEST";
        PasswordDto passwordDto = new PasswordDto(userDto.getPassword(), newPassword, newPasswordConfirm);
        String passwordJson = mapper.writeValueAsString(passwordDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(passwordJson)
        ).andExpect(mvcResult ->
                assertInstanceOf(PasswordsDoNotMatchException.class, mvcResult.getResolvedException())
        );
    }

    @Test
    public void testThatUserCanNotChangePasswordReturnsPasswordsAreSameException() throws Exception {
        userService.createUser(TestDataUtil.getUserEntityA());

        UserDto userDto = entityDtoMapper.mapTo(TestDataUtil.getUserEntityA());
        String userJson = mapper.writeValueAsString(userDto);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andReturn();

        String responseJson = result.getResponse().getContentAsString();
        JsonNode jsonNode = mapper.readTree(responseJson);
        String token = jsonNode.get("token").asText();

        // Old password is password
        String newPassword = "password";
        String newPasswordConfirm = "password";
        PasswordDto passwordDto = new PasswordDto(userDto.getPassword(), newPassword, newPasswordConfirm);
        String passwordJson = mapper.writeValueAsString(passwordDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(passwordJson)
        ).andExpect(mvcResult ->
                assertInstanceOf(PasswordsAreSameException.class, mvcResult.getResolvedException())
        );
    }
}
