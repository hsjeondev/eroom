package com.eroom.project.dto;

import com.eroom.employee.entity.Employee;
import com.eroom.project.entity.ProjectTodoElement;
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

    
    public ProjectTodoElementDto toDto(ProjectTodoElement projectTodoElement) {
        return ProjectTodoElementDto
                .builder()
                .todo_no(projectTodoElement.getTodo_no())
                .project_todo_list_no(projectTodoElement.getProjectTodoList().getProjectTodoListNo())
                .projectTodolist(projectTodoElement.getProjectTodoList())
                .employee_no(projectTodoElement.getEmployee().getEmployeeNo())
                .employee(projectTodoElement.getEmployee())
                .todo_title(projectTodoElement.getTodo_title())
                .emergency(projectTodoElement.getEmergency())
                .build();
    }

    public ProjectTodoElement toEntity(ProjectTodoList projectTodoList, Employee employee) {
        return ProjectTodoElement
                .builder()
                .todo_no(todo_no)
                .projectTodoList(projectTodoList)
                .employee(employee)
                .todo_title(todo_title)
                .emergency(emergency)
                .build();
    }
}
