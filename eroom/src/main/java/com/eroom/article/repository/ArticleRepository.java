package com.eroom.article.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.article.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long>{
	
	// 단일 조회
	Optional<Article> findByArticleNoAndVisibleYn(Long articleNo, String visibleYn);

	
	// 공지 게시판 조회
	List<Article> findBySeparatorCodeAndVisibleYnOrderByArticleRegDateDesc(String separatorCode, String visibleYn);
	
}
