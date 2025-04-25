package com.eroom.project.dto;

import java.time.LocalDate;
import java.util.List;

import com.eroom.project.entity.ProjectTodoList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProjectTodoListDto {

	private Long project_todo_list_no;
	
	private Long project_no;
	
	private String list_name;
	
	private int list_sequence;
	
	private String list_color;
	
	
	public ProjectTodoListDto toDto(ProjectTodoList projectTodoList) {
		return ProjectTodoListDto
				.builder()
				.project_todo_list_no(projectTodoList.getProjectTodoListNo())
				.project_no(projectTodoList.getProjectNo())
				.list_name(projectTodoList.getListName())
				.list_sequence(projectTodoList.getListSequence())
				.list_color(projectTodoList.getListColor())
				.build();
	}
	
	public ProjectTodoList toEntity() {
		return ProjectTodoList
				.builder()
				.projectTodoListNo(project_todo_list_no)
				.projectNo(project_no)
				.listName(list_name)
				.listSequence(list_sequence)
				.listColor(list_color)
				.build();
				
	}
}
