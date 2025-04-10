package com.eroom.attendance.dto;

import com.eroom.attendance.entity.AnnualLeave;

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
	private Long annual_leave_total;
	private Long annual_leave_used;
	private Long annual_leave_half_used;
	
	public AnnualLeave toEntity() {
		return AnnualLeave.builder()
						.annualLeaveNo(annual_leave_no)
						.annualLeaveTotal(annual_leave_total)
						.annualLeaveUsed(annual_leave_used)
						.annualLeaveHalfUsed(annual_leave_half_used)
						.build();
		
		// . employee(Employee.builder().employeeNo(employee_no).build())
	}
	
	
	public AnnualLeaveDto toDto(AnnualLeave annualLeave) {
		return AnnualLeaveDto.builder()
							.annual_leave_total(annualLeave.getAnnualLeaveTotal())
							.annual_leave_used(annualLeave.getAnnualLeaveUsed())
							.annual_leave_half_used(annualLeave.getAnnualLeaveHalfUsed())
							.build();
	}
}
