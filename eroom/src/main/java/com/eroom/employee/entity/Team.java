package com.eroom.employee.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="team")
public class Team {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long teamNo; // 팀 번호
	
	@Column(name="team_name")
	private String teamName; // 팀 이름
	@Column(name="team_order")
	private Long teamOrder; // 팀 순서
	
	@ManyToOne
	@JoinColumn(name="department_no")
	private Department department; // 부서번호

	@OneToMany(mappedBy = "team")
	private List<Employee> employees;
	
	
	
}
