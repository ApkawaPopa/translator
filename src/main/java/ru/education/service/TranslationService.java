package ru.education.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class TranslationService {
    @Value("${executor.max_threads}")
    private int MAX_THREADS;

    @Value("${api.key}")
    private String API_KEY;
    @Value("${api.host}")
    private String API_HOST;
    @Value("${api.endpoint}")
    private String API_ENDPOINT;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public String translateText(
        String sourceLanguageCode,
        String targetLanguageCode,
        String text
    ) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

        String[] words = text.split(" ");
        List<Future<String>> futures = new ArrayList<>();
        for (String word : words) {
            futures.add(
                executorService.submit(
                    () -> translateWord(sourceLanguageCode, targetLanguageCode, word)
                )
            );
        }

        List<String> translatedWords = new ArrayList<>();
        for (Future<String> future : futures) {
            translatedWords.add(future.get());
        }

        executorService.shutdown();

        return String.join(" ", translatedWords);
    }

    private String translateWord(
        String sourceLanguageCode,
        String targetLanguageCode,
        String word
    ) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-rapidapi-key", API_KEY);
        headers.add("x-rapidapi-host", API_HOST);

        RequestData requestData = new RequestData(
            sourceLanguageCode,
            targetLanguageCode,
            word
        );
        String requestDataString = objectMapper.writeValueAsString(requestData);

        HttpEntity<String> request = new HttpEntity<>(requestDataString, headers);

        String responseDataString = restTemplate.postForObject(
            API_ENDPOINT,
            request,
            String.class
        );

        ResponseData responseData = objectMapper.readValue(responseDataString, ResponseData.class);

        return responseData.translatedText;
    }
    
    record RequestData(
        @JsonProperty("from") String sourceLanguageCode,
        @JsonProperty("to") String targetLanguageCode,
        @JsonProperty("html") String text
    ) {
    }

    record ResponseData(@JsonProperty("trans") String translatedText) {
    }
}
