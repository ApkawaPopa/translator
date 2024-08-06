package ru.education.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.education.repository.TranslationRepository;
import ru.education.service.TranslationService;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

@RestController
public class TranslationController {
    @Autowired
    private TranslationRepository repository;
    @Autowired
    private TranslationService service;

    @RequestMapping(value = "/translate", method = RequestMethod.POST)
    public String translate(
        HttpServletRequest userRequest, @Valid @RequestBody UserRequestData userRequestData
    ) throws ExecutionException, InterruptedException, SQLException {
        String userAddress = userRequest.getRemoteAddr();
        String text = userRequestData.text;
        String translatedText = service.translateText(
            userRequestData.sourceLanguageCode,
            userRequestData.targetLanguageCode,
            userRequestData.text
        );

        repository.insert(userAddress, text, translatedText);

        return translatedText;
    }

    public record UserRequestData(
        @NotBlank(message = "sourceLanguageCode should not be blank") String sourceLanguageCode,
        @NotBlank(message = "targetLanguageCode should not be blank") String targetLanguageCode,
        @NotNull(message = "text should not be null") String text
    ) {
    }
}
