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
@Table(name="directory")
public class EmployeeDirectory {
	
	@Id
	@Column(name="directory_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long directoryNo; // 직원 주소록 번호
	
	@Column(name = "directory_email")
	private String employeeDirectoryEmail; // 이메일
	@Column(name = "directory_phone")
	private String employeeDirectoryPhone; // 휴대폰 번호
	
	@OneToOne
	@JoinColumn(name = "employee_no")
	private Employee employee; // 사번
	

}
