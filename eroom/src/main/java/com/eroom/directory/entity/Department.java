//package com.eroom.directory.entity;
//
//
//import java.util.List;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//@Setter
//@ToString
//@Builder
//
//@Entity
//@Table(name="department")
//public class Department {
//	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long departmentNo; // 부서 번호
//	
//	@Column(name="department_name")
//	private String departmentName; // 부서 이름
//	@Column(name="department_order")
//	private Long departmentOrder; // 부서 번호
//	
//	@OneToMany(mappedBy = "department")
//	private List<Employee> employees;
//
//}
