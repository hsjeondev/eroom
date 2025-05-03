package com.eroom.article.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.article.dto.ArticleDto;
import com.eroom.article.entity.Article;
import com.eroom.article.repository.ArticleRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
	
	 private final ArticleRepository articleRepository;
	 private final EmployeeRepository employeeRepository;
	 
	 // 공지 게시판 조회
	 public List<Article> getNoticeArticles() {
	        return articleRepository.findBySeparatorCodeAndVisibleYnOrderByArticleRegDateDesc("B001", "Y");
	    }
	 // 공지 게시판 작성 
	 public int createArticle(ArticleDto articleDto, Long employeeNo) {
	        // Employee 엔티티 가져오기
	        Employee employee = employeeRepository.findById(employeeNo)
	                .orElseThrow(() -> new IllegalArgumentException("해당 사번의 사용자를 찾을 수 없습니다."));
	
	        Article article = Article.builder()
	                .employee(employee)
	                .articleTitle(articleDto.getArticle_title())
	                .articleContent(articleDto.getArticle_content())
	                .separatorCode("B001")
	                .order(articleDto.getOrder())
	                .visibleYn("Y")
	                .articleCreator(employeeNo)
	                .articleEditor(employeeNo)
	                .articleRegDate(LocalDateTime.now())
	                // .articleModDate(LocalDateTime.now())
	                .build();
	
	        articleRepository.save(article);
	        return 1;
	 }
}