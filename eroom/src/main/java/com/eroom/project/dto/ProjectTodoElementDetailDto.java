package com.eroom.project.dto;

import com.eroom.project.entity.ProjectTodoElement;
import com.eroom.project.entity.ProjectTodoElementDetail;

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
@Builder
@ToString
public class ProjectTodoElementDetailDto {

	private Long todo_detail_no;
	private Long todo_element_no;
	private String todo_content;
	private String status;
	private Long project_no;
	private String visible_yn;

	public static ProjectTodoElementDetailDto toDto(ProjectTodoElementDetail entity) {
		return ProjectTodoElementDetailDto.builder()
				.todo_detail_no(entity.getTodoDetailNo())
				.todo_element_no(entity.getProjectTodoElement() != null ? entity.getProjectTodoElement().getTodoNo() : null)
				.todo_content(entity.getTodoContent())
				.status(entity.getStatus())
				.project_no(entity.getProjectNo())
				.visible_yn(entity.getVisibleYn())
				.build();
	}

	public ProjectTodoElementDetail toEntity(ProjectTodoElement projectTodoElement) {
		return ProjectTodoElementDetail.builder()
				.todoDetailNo(todo_detail_no)
				.projectTodoElement(projectTodoElement)
				.todoContent(todo_content)
				.status(status)
				.projectNo(project_no)
				.visibleYn(visible_yn)
				.build();
	}
}
