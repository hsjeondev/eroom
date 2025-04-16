package com.eroom.project.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@ToString
@Builder
@Entity
@Table(name="project")
public class Project {

	@Id
	@Column(name="project_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectNo;
	
	@Column(name="project_name")
	private String projectName;
	
	@Column(name="project_start")
	private LocalDateTime projectStart;
	
	@Column(name="project_end")
	private LocalDateTime projectEnd;
	
	@Column(name="proceed")
	private String proceed;
	
	@Column(name="project_github_url")
	private String projectGithubUrl;
	
	@Column(name="project_github_token")
	private String projectGithubToken;
	
	@OneToMany(mappedBy="project")
	private List<ProjectMember> projectMembers;
	
}
