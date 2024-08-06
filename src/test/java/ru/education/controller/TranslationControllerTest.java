package ru.education.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.education.repository.TranslationRepository;
import ru.education.service.TranslationService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TranslationController.class)
public class TranslationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TranslationService translationService;

    @MockBean
    private TranslationRepository translationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private TranslationController.UserRequestData userRequestData;

    @BeforeEach
    public void setUp() {
        userRequestData = new TranslationController.UserRequestData("en", "ru", "hello world");
    }

    @Test
    public void testTranslate() throws Exception {
        String translatedText = "привет мир";
        when(translationService.translateText("en", "ru", "hello world")).thenReturn(translatedText);

        mockMvc.perform(post("/translate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestData)))
            .andExpect(status().isOk())
            .andExpect(content().string(translatedText));
    }
}
