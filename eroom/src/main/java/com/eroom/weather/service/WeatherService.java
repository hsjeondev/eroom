package com.eroom.weather.service;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eroom.weather.dto.WeatherDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public WeatherService() {
        // RestTemplate의 기본 인코딩을 UTF-8로 설정
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public String getWeatherByCity(String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric&lang=kr", apiUrl, city, apiKey);
        String response = restTemplate.getForObject(url, String.class);
        System.out.println("Weather API response: " + response);
        return restTemplate.getForObject(url, String.class);
    }

    public WeatherDto getWeatherByCoord(double lat, double lon) {
        // API 요청
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&lang=kr&units=metric";
        
        // 응답 파싱
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            WeatherDto weatherDto = objectMapper.readValue(response.getBody(), WeatherDto.class);
            return weatherDto;
        } catch (JsonMappingException e) {
            // 예외 처리 로직 추가
            System.out.println("JSON 파싱 오류: " + e.getMessage());
            return null;
        } catch (Exception e) {
            // 일반적인 예외 처리
            System.out.println("오류: " + e.getMessage());
            return null;
        }
    }
    
}