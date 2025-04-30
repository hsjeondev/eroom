package com.eroom.project.dto;

import java.util.ArrayList;
import java.util.List;

import com.eroom.employee.entity.Employee;
import com.eroom.project.entity.ProjectTodoElement;
import com.eroom.project.entity.ProjectTodoElementDetail;
import com.eroom.project.entity.ProjectTodoList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTodoElementDto {

    private Long todo_no;

    private Long project_todo_list_no;
    
    private ProjectTodoList projectTodolist;

    private Long employee_no;
    
    private Employee employee;

    private String todo_title;

    private String emergency;
    
    private int element_sequence;
    
    private List<ProjectTodoElementDetailDto> todo_element_details;
    
    private int totalCount;
    
    private int completedCount;
    
    private String visible_yn;


    
    public ProjectTodoElementDto toDto(ProjectTodoElement projectTodoElement) {
        List<ProjectTodoElementDetailDto> detailDtoList = null;
        
        
        if (projectTodoElement.getTodoElementDetails() != null) {
            detailDtoList = new ArrayList<>();
            for (ProjectTodoElementDetail detail : projectTodoElement.getTodoElementDetails()) {
                ProjectTodoElementDetailDto detailDto = ProjectTodoElementDetailDto.builder()
                        .todo_detail_no(detail.getTodoDetailNo())
                        .todo_element_no(detail.getProjectTodoElement() != null ? detail.getProjectTodoElement().getTodoNo() : null)
                        .todo_content(detail.getTodoContent())
                        .status(detail.getStatus())
                        .project_no(detail.getProjectNo())
                        .visible_yn(detail.getVisibleYn())
                        .build();
                detailDtoList.add(detailDto);
            }
        }

        return ProjectTodoElementDto.builder()
                .todo_no(projectTodoElement.getTodoNo())
                .project_todo_list_no(projectTodoElement.getProjectTodoList().getProjectTodoListNo())
                .projectTodolist(projectTodoElement.getProjectTodoList())
                .employee_no(projectTodoElement.getEmployee().getEmployeeNo())
                .employee(projectTodoElement.getEmployee())
                .todo_title(projectTodoElement.getTodoTitle())
                .emergency(projectTodoElement.getEmergency())
                .element_sequence(projectTodoElement.getElementSequence())
                .todo_element_details(detailDtoList)
                .visible_yn(projectTodoElement.getVisibleYn())
                .build();
    }


    public ProjectTodoElement toEntity(ProjectTodoList projectTodoList, Employee employee) {
        ProjectTodoElement projectTodoElement = ProjectTodoElement.builder()
                .todoNo(todo_no)
                .projectTodoList(projectTodoList)
                .employee(employee)
                .todoTitle(todo_title)
                .emergency(emergency)
                .elementSequence(element_sequence)
                .visibleYn(visible_yn)
                .build();

        if (todo_element_details != null) {
            List<ProjectTodoElementDetail> details = new ArrayList<>();
            for (ProjectTodoElementDetailDto detailDto : todo_element_details) {
                ProjectTodoElementDetail detail = ProjectTodoElementDetail.builder()
                        .todoDetailNo(detailDto.getTodo_detail_no())
                        .projectTodoElement(projectTodoElement)
                        .todoContent(detailDto.getTodo_content())
                        .status(detailDto.getStatus())
                        .projectNo(detailDto.getProject_no())
                        .visibleYn(detailDto.getVisible_yn())
                        .build();
                details.add(detail);
            }
            projectTodoElement.setTodoElementDetails(details);
        }

        return projectTodoElement;
    }

}
