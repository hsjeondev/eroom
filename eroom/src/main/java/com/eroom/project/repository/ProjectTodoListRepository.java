package com.eroom.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.eroom.project.entity.ProjectTodoList;

public interface ProjectTodoListRepository extends JpaRepository<ProjectTodoList, Long>, JpaSpecificationExecutor<ProjectTodoList> {

}
