package com.eroom.project.dto;

import java.util.ArrayList;
import java.util.List;

import com.eroom.project.entity.ProjectTodoElement;
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
	
	private List<ProjectTodoElementDto> projectTodoElements;
	
	private String visible_yn;
	
	private int todoElementCount;

	
	
	public ProjectTodoListDto toDto(ProjectTodoList projectTodoList) {
	    ProjectTodoListDto dto = ProjectTodoListDto.builder()
	            .project_todo_list_no(projectTodoList.getProjectTodoListNo())
	            .project_no(projectTodoList.getProjectNo())
	            .list_name(projectTodoList.getListName())
	            .list_sequence(projectTodoList.getListSequence())
	            .list_color(projectTodoList.getListColor())
	            .visible_yn(projectTodoList.getVisibleYn())
	            .build();

	    if (projectTodoList.getProjectTodoElements() != null) {
	        List<ProjectTodoElementDto> elementDtoList = new ArrayList<>();
	        for (ProjectTodoElement element : projectTodoList.getProjectTodoElements()) {
	            ProjectTodoElementDto elementDto = new ProjectTodoElementDto();
	            elementDto = elementDto.toDto(element);
	            elementDtoList.add(elementDto);
	        }
	        dto.setProjectTodoElements(elementDtoList);
	    }


	    return dto;
	}

	public ProjectTodoList toEntity() {
		return ProjectTodoList
				.builder()
				.projectTodoListNo(project_todo_list_no)
				.projectNo(project_no)
				.listName(list_name)
				.listSequence(list_sequence)
				.listColor(list_color)
				.visibleYn(visible_yn)
				.build();
				
	}
}
