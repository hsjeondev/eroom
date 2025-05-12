package com.eroom.news.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class NewsApiService {

    @Value("${newsapi.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private List<Map<String, Object>> cachedArticles = new ArrayList<>();
    private LocalDateTime lastFetchTime = null;

    public synchronized List<Map<String, Object>> getNews(String query, String language, int page, int size) {
        if (lastFetchTime == null || lastFetchTime.plusHours(1).isBefore(LocalDateTime.now())) {
            String url = String.format("https://newsapi.org/v2/everything?q=%s&language=%s&page=%d&pageSize=%d&apiKey=%s",
                    query, language, page, size, apiKey);
            try {
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                List<Map<String, Object>> articles = (List<Map<String, Object>>) response.get("articles");
                if (articles != null && !articles.isEmpty()) {
                    cachedArticles = articles;
                    lastFetchTime = LocalDateTime.now();
                }
            } catch (HttpClientErrorException.TooManyRequests e) {
                System.err.println("뉴스 API 호출 횟수 초과(429): 기존 뉴스 유지");
                // 캐시 유지
            } catch (Exception e) {
                System.err.println("뉴스 API 호출 실패: " + e.getMessage());
            }
        }

        return cachedArticles;
    }
}


