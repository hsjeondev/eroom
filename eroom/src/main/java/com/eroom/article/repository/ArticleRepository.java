package com.eroom.article.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.article.entity.Article;

import jakarta.transaction.Transactional;

public interface ArticleRepository extends JpaRepository<Article, Long>{
	
	// 단일 조회
	Optional<Article> findByArticleNoAndVisibleYn(Long articleNo, String visibleYn);

	
	// 공지 게시판 조회
	 List<Article> findBySeparatorCodeAndVisibleYnOrderByArticleRegDateDesc(String separatorCode, String visibleYn);
	//List<Article> findBySeparatorCodeAndVisibleYnOrderByArticleEmergencyYnDescArticleRegDateDesc(String separatorCode, String visibleYn);

	// 삭제 쿼리
    @Modifying
    @Transactional  // 트랜잭션 관리 추가
    @Query("UPDATE Article a SET a.visibleYn = :visibleYn WHERE a.articleNo = :articleNo")
    void updateArticleVisibilityStatus(@Param("articleNo") Long articleNo, @Param("visibleYn") String visibleYn);
}
