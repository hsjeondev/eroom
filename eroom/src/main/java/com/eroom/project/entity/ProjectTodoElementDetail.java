package com.eroom.project.entity;

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
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name="project_todo_element_detail")
public class ProjectTodoElementDetail {
	
	@Id
	@Column(name="todo_detail_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long todoDetailNo;
	
	@ManyToOne
	@JoinColumn(name = "todo_element_no")
	private ProjectTodoElement projectTodoElement;
	
	@Column(name="todo_content")
	private String todoContent;
	
	@Column(name="status")
	private String status;
	
	@Column(name="project_no")
	private Long projectNo;
	
	@Column(name="visible_yn")
	private String visibleYn;

}
