package com.eroom.news.controller;

import com.eroom.news.service.NewsApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsController {

    @Autowired
    private NewsApiService newsApiService;

    @GetMapping("/api/news")
    public ResponseEntity<?> getNews(@RequestParam(name = "query") String query, 
                                      @RequestParam(name = "language", defaultValue = "ko") String language, 
                                      @RequestParam(name = "page", defaultValue = "1") int page, 
                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        // News API 서비스에서 받아온 데이터를 JSON 형식으로 반환
        return ResponseEntity.ok(newsApiService.getNews(query, language, page, size));
    }
}
