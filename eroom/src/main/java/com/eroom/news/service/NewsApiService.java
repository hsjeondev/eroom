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

    public List<Map<String, Object>> getNews(String query, String language, int page, int size) {
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
            } catch (Exception e) {
                System.err.println("뉴스 API 호출 실패: " + e.getMessage());
            }
        }

        // ✅ 캐시가 아예 비어 있으면 디폴트 뉴스 제공
        if (cachedArticles == null || cachedArticles.isEmpty()) {
            Map<String, Object> defaultArticle = Map.of(
                "title", "“눈 감았다 뜨니 세상 바뀌어 있었다”",
                "description", "AI 임팩트, 두려움과 기대 속에 다가온 기술 혁명… GPT가 만든 세계, 우리는 준비됐나?",
                "urlToImage", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/57/Artificial_Intelligence_%26_AI_%26_Machine_Learning_-_30212411048.jpg/640px-Artificial_Intelligence_%26_AI_%26_Machine_Learning_-_30212411048.jpg",
                "url", "https://www.kmib.co.kr/article/view.asp?arcid=0028078159&code=61141111&cp=nv"
            );
            return List.of(defaultArticle);
        }

        return cachedArticles;
    }

}


