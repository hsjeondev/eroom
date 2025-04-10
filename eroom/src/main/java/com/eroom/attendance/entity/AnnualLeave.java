package com.eroom.attendance.entity;

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

@Entity
@Table(name="annual_leave")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnualLeave {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="annual_leave_no")
	private Long annualLeaveNo; // 연차 번호
	
	@Column(name="annual_leave_total")
	private Long annualLeaveTotal; // 총 연차
	
	@Column(name="annual_leave_used")
	private Long annualLeaveUsed; // 사용 연차
	
	@Column(name="annual_leave_half_used")
	private Long annualLeaveHalfUsed; // 사용 반차
	
//	@OneToOne
//	@JoinColumn(name="employee_no")
//	private Employee employee; // 사번
	
}
