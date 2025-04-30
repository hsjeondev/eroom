package com.eroom.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.project.entity.ProjectTodoList;

public interface ProjectTodoListRepository extends JpaRepository<ProjectTodoList, Long>, JpaSpecificationExecutor<ProjectTodoList> {

	List<ProjectTodoList> findByProjectNoOrderByListSequenceAsc(Long projectNo);
	
	@Query("SELECT DISTINCT l FROM ProjectTodoList l " +
		       "LEFT JOIN FETCH l.projectTodoElements e " +
		       "WHERE l.projectNo = :projectNo AND l.visibleYn = 'Y' " +
		       "ORDER BY l.listSequence ASC")
		List<ProjectTodoList> findWithElementsByProjectNo(@Param("projectNo") Long projectNo);

	
	@Query("SELECT p FROM ProjectTodoList p WHERE p.projectNo = :projectNo AND p.listSequence = :listSequence")
	Optional<ProjectTodoList> findByProjectNoAndListSequence(@Param("projectNo") Long projectNo, 
	                                                          @Param("listSequence") int listSequence);
	
	@Query("SELECT l FROM ProjectTodoList l WHERE l.projectNo = :projectNo AND l.listSequence > :seq AND l.visibleYn = 'Y' ORDER BY l.listSequence ASC")
	List<ProjectTodoList> findNextList(@Param("projectNo") Long projectNo, @Param("seq") int seq);

	@Query("SELECT l FROM ProjectTodoList l WHERE l.projectNo = :projectNo AND l.listSequence < :seq AND l.visibleYn = 'Y' ORDER BY l.listSequence DESC")
	List<ProjectTodoList> findPrevList(@Param("projectNo") Long projectNo, @Param("seq") int seq);

	
	@Query("SELECT l, COUNT(CASE WHEN e.visibleYn = 'Y' THEN 1 END) " +
		       "FROM ProjectTodoList l " +
		       "LEFT JOIN l.projectTodoElements e " +
		       "WHERE l.projectNo = :projectNo AND l.visibleYn = 'Y' " +
		       "GROUP BY l " +
		       "ORDER BY l.listSequence")
	List<Object[]> findListWithElementCountByProjectNo(@Param("projectNo") Long projectNo);
		
	// 기존 findByProjectNoOrderByListSequenceAsc는 모든 리스트를 가져오기 때문에,
	// visible_yn = 'Y' 조건을 추가한 새 메서드를 정의합니다.
	List<ProjectTodoList> findByProjectNoAndVisibleYnOrderByListSequenceAsc(Long projectNo, String visibleYn);






}
