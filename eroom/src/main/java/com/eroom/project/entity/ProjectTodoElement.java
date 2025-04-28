package com.eroom.project.entity;

import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="project_todo_element")
public class ProjectTodoElement {

	@Id
	@Column(name="todo_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long todo_no;
	
	@ManyToOne
	@JoinColumn(name="project_todo_list_no")
	private ProjectTodoList projectTodoList;
	
	@ManyToOne
	@JoinColumn(name="employee_no")
	private Employee employee;
	
	@Column(name="todo_title")
	private String todo_title;
	
	@Column(name="emergency")
	private String emergency; 
}
