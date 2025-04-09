package com.eroom.directory.entity;

import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name="employee_directory")
public class EmployeeDirectory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long employeeDirectoryNo; // 직원 주소록 번호
	
	@Column(name = "employee_directory_email")
	private String employeeDirectoryEmail; // 이메일
	@Column(name = "employee_directory_phone")
	private String employeeDirectoryPhone; // 휴대폰 번호
	
	@OneToOne
	@JoinColumn(name = "employee_no")
	private Employee employee; // 사번
	

}
