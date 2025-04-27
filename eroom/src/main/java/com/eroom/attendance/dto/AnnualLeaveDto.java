package com.eroom.attendance.dto;

import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.employee.entity.Employee;

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
public class AnnualLeaveDto {

	private Long annual_leave_no;
	private Long employee_no;
	private Double annual_leave_total;
	private Double annual_leave_used;
	private Long year; // 연차 년도
	
	// 잔여 연차
	private Double annual_leave_remain;
	
	public AnnualLeave toEntity() {
		return AnnualLeave.builder()
						.annualLeaveNo(annual_leave_no)
						.annualLeaveTotal(annual_leave_total)
						.annualLeaveUsed(annual_leave_used)
						.year(year)
						.employee(Employee.builder().employeeNo(employee_no).build())
						.build();
		
	}
	
	
	public AnnualLeaveDto toDto(AnnualLeave annualLeave) {
		return AnnualLeaveDto.builder()
				            .annual_leave_no(annualLeave.getAnnualLeaveNo())
				            .year(annualLeave.getYear())
							.annual_leave_total(annualLeave.getAnnualLeaveTotal())
							.annual_leave_used(annualLeave.getAnnualLeaveUsed())
							.annual_leave_remain(annualLeave.getAnnualLeaveTotal() - annualLeave.getAnnualLeaveUsed())
							.build();
	}
}
