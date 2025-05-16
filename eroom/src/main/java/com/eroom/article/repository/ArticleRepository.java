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

	List<Article> findByArticleEmergencyYnAndVisibleYnOrderByArticleRegDateDesc(String emergencyYn, String visibleYn);
    List<Article> findByArticleEmergencyYnNotAndVisibleYnOrderByArticleRegDateDesc(String emergencyYn, String visibleYn);
	// 공지 게시판 조회
	 // List<Article> findBySeparatorCodeAndVisibleYnOrderByArticleRegDateDesc(String separatorCode, String visibleYn);
	//List<Article> findBySeparatorCodeAndVisibleYnOrderByArticleEmergencyYnDescArticleRegDateDesc(String separatorCode, String visibleYn);
//	 @Query("SELECT a FROM Article a " +
//		       "WHERE a.separatorCode = :separatorCode AND a.visibleYn = :visibleYn " +
//		       "ORDER BY " +
//		       "CASE WHEN a.articleEmergencyYn = 'Y' THEN 0 ELSE 1 END, " + // 긴급이면 0 → 먼저 정렬
//		       "a.articleEmergencyYn DESC, " +                              // (안전장치)
//		       "CASE WHEN a.articleEmergencyYn = 'Y' THEN a.articleModDate ELSE a.articleRegDate END DESC")
//		List<Article> findOrderedArticles(@Param("separatorCode") String separatorCode, @Param("visibleYn") String visibleYn);
//	@Query("SELECT a FROM Article a " +
//		       "WHERE a.separatorCode = :separatorCode AND a.visibleYn = :visibleYn " +
//		       "ORDER BY " +
//		       "a.articleEmergencyYn DESC, " +                 // 긴급 여부 먼저 (Y > N)
//		       "a.articleModDate DESC, " +                     // 이후 수정일 기준 정렬
//		       "a.articleRegDate DESC")                        // 수정일이 같으면 등록일 기준
//		List<Article> findOrderedArticles(@Param("separatorCode") String separatorCode, @Param("visibleYn") String visibleYn);
	@Query("SELECT a FROM Article a " +
		       "WHERE a.separatorCode = :separatorCode AND a.visibleYn = :visibleYn " +
		       "ORDER BY " +
		       "CASE WHEN a.articleEmergencyYn = 'Y' THEN 0 ELSE 1 END, " +
		       "a.articleRegDate DESC")
		List<Article> findNoticeBoard(@Param("separatorCode") String separatorCode, @Param("visibleYn") String visibleYn);
	// 삭제 쿼리
    @Modifying
    @Transactional  // 트랜잭션 관리 추가
    @Query("UPDATE Article a SET a.visibleYn = :visibleYn WHERE a.articleNo = :articleNo")
    void updateArticleVisibilityStatus(@Param("articleNo") Long articleNo, @Param("visibleYn") String visibleYn);
}
