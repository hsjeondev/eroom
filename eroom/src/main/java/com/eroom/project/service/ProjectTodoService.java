package com.eroom.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.project.dto.ProjectTodoListDto;
import com.eroom.project.entity.ProjectTodoList;
import com.eroom.project.repository.ProjectTodoListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectTodoService {
	
	private final ProjectTodoListRepository projectTodoListRepository;
	
	public List<ProjectTodoListDto> findByProjectNo(Long projectNo) {
	    // (1) 프로젝트 번호로 필터링할 리포지토리 메서드가 있으면 findByProjectNo를 사용하세요.
	    List<ProjectTodoList> list = projectTodoListRepository.findAll(); 
	    // → 예: projectTodoListRepository.findByProjectNo(projectNo);

	    List<ProjectTodoListDto> dtolist = new ArrayList<>();

	    for (ProjectTodoList projectTodoList : list) {
	        ProjectTodoListDto dto = ProjectTodoListDto.builder()
	            .project_todo_list_no(projectTodoList.getProjectTodoListNo())
	            .project_no(projectTodoList.getProjectNo())
	            .list_name(projectTodoList.getListName())
	            .list_sequence(projectTodoList.getListSequence())
	            .list_color(projectTodoList.getListColor())
	            .build();
	        dtolist.add(dto);
	    }

	    return dtolist;
	}


	
}
