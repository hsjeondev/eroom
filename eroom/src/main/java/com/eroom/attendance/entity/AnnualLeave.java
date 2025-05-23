package com.eroom.attendance.entity;

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

@Entity
@Table(name="annual_leave")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnnualLeave {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="annual_leave_no")
	private Long annualLeaveNo; // 연차 번호
	
	@Column(name="year")
	private Long year; // 연차 년도
	
	@Column(name="annual_leave_total")
	private Double annualLeaveTotal; // 총 연차
	
	@Column(name="annual_leave_used")
	private Double annualLeaveUsed; // 사용 연차
	
	@OneToOne
	@JoinColumn(name="employee_no")
	private Employee employee; // 사번
	
}
