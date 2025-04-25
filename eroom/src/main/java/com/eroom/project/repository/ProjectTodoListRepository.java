package com.eroom.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.eroom.project.entity.ProjectTodoList;

public interface ProjectTodoListRepository extends JpaRepository<ProjectTodoList, Long>, JpaSpecificationExecutor<ProjectTodoList> {

	List<ProjectTodoList> findByProjectNoOrderByListSequenceAsc(Long projectNo);
}
