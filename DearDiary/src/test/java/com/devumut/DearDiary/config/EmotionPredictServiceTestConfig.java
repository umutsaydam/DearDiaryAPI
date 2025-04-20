package com.devumut.DearDiary.config;

import com.devumut.DearDiary.services.EmotionPredictService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.when;

@TestConfiguration
public class EmotionPredictServiceTestConfig {

    @Bean
    public EmotionPredictService emotionPredictService() {
        EmotionPredictService mock = Mockito.mock(EmotionPredictService.class);
        when(mock.predictEmotionFromText(Mockito.anyString())).thenReturn(2);
        return mock;
    }
}