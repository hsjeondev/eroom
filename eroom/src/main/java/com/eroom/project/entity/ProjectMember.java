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
import lombok.ToString;

@Entity
@Table(name="project_member")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMember {

	@Id
	@Column(name="project_member_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectMemberNo;
	
	@ManyToOne
	@JoinColumn(name="project_no")
	private Project project;
	
	@ManyToOne
	@JoinColumn(name="employee_no")
	private Employee employee;
	
	@Column(name="project_manager")
	private String projectManager;
	
	@Column(name="github_username")
	private String githubUsername;
	
}
