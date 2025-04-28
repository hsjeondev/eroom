package com.eroom.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.project.entity.ProjectTodoElement;
import com.eroom.project.entity.ProjectTodoList;

public interface ProjectTodoElementRepository extends JpaRepository<ProjectTodoElement, Long>, JpaSpecificationExecutor<ProjectTodoElement> {

	List<ProjectTodoElement> findByProjectTodoListOrderByElementSequenceAsc(ProjectTodoList projectTodoList);
	
	@Query("SELECT MAX(e.elementSequence) FROM ProjectTodoElement e WHERE e.projectTodoList.projectTodoListNo = :projectTodoListNo")
	Integer findMaxElementSequence(@Param("projectTodoListNo") Long projectTodoListNo);


}
