package com.eroom.project.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@Table(name="project_todo_list")
public class ProjectTodoList {

	@Id
	@Column(name="project_todo_list_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectTodoListNo;
	
	@Column(name="project_no")
	private Long projectNo;
	
	@Column(name="list_name")
	private String listName;
	
	@Column(name="list_sequence")
	private int listSequence;
	
	@Column(name="list_color")
	private String listColor;
	
	@OneToMany(mappedBy = "projectTodoList")
	@OrderBy("elementSequence ASC")
    private List<ProjectTodoElement> projectTodoElements;
	
	@Column(name="visible_yn")
	private String visibleYn;
}
