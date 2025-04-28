package com.eroom.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.project.entity.ProjectTodoList;

public interface ProjectTodoListRepository extends JpaRepository<ProjectTodoList, Long>, JpaSpecificationExecutor<ProjectTodoList> {

	List<ProjectTodoList> findByProjectNoOrderByListSequenceAsc(Long projectNo);
	
	@Query("SELECT l FROM ProjectTodoList l LEFT JOIN FETCH l.projectTodoElements WHERE l.projectNo = :projectNo ORDER BY l.listSequence ASC")
	List<ProjectTodoList> findWithElementsByProjectNo(@Param("projectNo") Long projectNo);

}
