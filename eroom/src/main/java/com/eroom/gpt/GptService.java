package com.eroom.gpt;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GptService {

	@Value("${gpt.key}")
    private String apiKey;
	
    private final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String askGpt(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 메시지 구성
        GptMessageDto message = new GptMessageDto("user", prompt);
        GptRequestDto request = new GptRequestDto("gpt-4o-mini", List.of(message));

        // HTTP 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<GptRequestDto> entity = new HttpEntity<>(request, headers);

        // 요청 보내고 응답 받기
        ResponseEntity<GptResponseDto> response = restTemplate.exchange(
            API_URL,
            HttpMethod.POST,
            entity,
            GptResponseDto.class
        );

        return response.getBody()
                       .getChoices()
                       .get(0)
                       .getMessage()
                       .getContent();
    }
}
