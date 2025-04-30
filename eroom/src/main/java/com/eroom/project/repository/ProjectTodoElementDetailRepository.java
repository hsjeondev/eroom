package com.eroom.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.eroom.project.entity.ProjectTodoElementDetail;

public interface ProjectTodoElementDetailRepository extends JpaRepository<ProjectTodoElementDetail, Long>, JpaSpecificationExecutor<ProjectTodoElementDetail>  {

	int countByProjectTodoElement_TodoNo(Long todoElementNo);

	int countByProjectTodoElement_TodoNoAndStatus(Long todoElementNo, String status);
	
	List<ProjectTodoElementDetail> findByProjectTodoElement_TodoNoAndVisibleYn(Long todoElementNo, String visibleYn);
	
	int countByProjectTodoElement_TodoNoAndVisibleYn(Long todoNo, String visibleYn);

	int countByProjectTodoElement_TodoNoAndStatusAndVisibleYn(Long todoNo, String status, String visibleYn);

}
