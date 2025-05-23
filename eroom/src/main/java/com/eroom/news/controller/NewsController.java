package com.eroom.news.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eroom.news.service.NewsApiService;

@RestController
public class NewsController {

    @Autowired
    private NewsApiService newsApiService;

    @GetMapping("/api/news")
    public ResponseEntity<?> getNews(
            @RequestParam(name = "query", defaultValue = "IT") String query,
            @RequestParam(name = "language", defaultValue = "ko") String language) {

        List<Map<String, Object>> newsList = newsApiService.getNews(query, language, 1, 10);
        return ResponseEntity.ok(Map.of("articles", newsList)); 
    }


}
