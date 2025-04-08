package com.eroom.directory.dto;


import com.eroom.directory.entity.Department;

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
public class DepartmentDto {
	private Long department_no;
	private String department_name;
	private Long department_order;
	
	public Department toEntity() {
		return Department.builder()
				.departmentNo(department_no)
				.departmentName(department_name)
				.departmentOrder(department_no)
				.build();
	}
	
	public DepartmentDto toDto(Department dep) {
		return DepartmentDto.builder()
				.department_no(dep.getDepartmentNo())
				.department_name(dep.getDepartmentName())
				.department_order(dep.getDepartmentOrder())
				.build();
	}
}
