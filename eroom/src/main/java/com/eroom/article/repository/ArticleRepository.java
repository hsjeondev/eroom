package com.eroom.article.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.article.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long>{
	
	// 공지 게시판 조회
	List<Article> findBySeparatorCodeAndVisibleYnOrderByArticleRegDateDesc(String separatorCode, String visibleYn);
	
}
