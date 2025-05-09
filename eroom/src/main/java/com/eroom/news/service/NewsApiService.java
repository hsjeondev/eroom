package com.eroom.news.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class NewsApiService {

    @Value("${newsapi.key}")
    private String apiKey;  // application.properties에서 가져온 NewsAPI 키

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getNews(String query, String language, int page, int size) {
        // NewsAPI URL 구성
        String url = String.format("https://newsapi.org/v2/everything?q=%s&language=%s&page=%d&pageSize=%d&apiKey=%s", 
                                    query, language, page, size, apiKey);

        // API 요청하여 JSON 데이터를 Map으로 변환
        return restTemplate.getForObject(url, Map.class);
    }
}
