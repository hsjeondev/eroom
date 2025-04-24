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

	private Long annualLeaveNo;
	private Long employeeNo;
	private Double annualLeaveTotal;
	private Double annualLeaveUsed;
	private Long year; // 연차 년도
	
	// 잔여 연차
	private Double annualLeaveRemain;
	
	public AnnualLeave toEntity() {
		return AnnualLeave.builder()
						.annualLeaveNo(annualLeaveNo)
						.annualLeaveTotal(annualLeaveTotal)
						.annualLeaveUsed(annualLeaveUsed)
						.year(year)
						.employee(Employee.builder().employeeNo(employeeNo).build())
						.build();
		
	}
	
	
	public AnnualLeaveDto toDto(AnnualLeave annualLeave) {
		return AnnualLeaveDto.builder()
				            .annualLeaveNo(annualLeave.getAnnualLeaveNo())
				            .year(annualLeave.getYear())
							.annualLeaveTotal(annualLeave.getAnnualLeaveTotal())
							.annualLeaveUsed(annualLeave.getAnnualLeaveUsed())
							.annualLeaveRemain(annualLeave.getAnnualLeaveTotal() - annualLeave.getAnnualLeaveUsed())
							.build();
	}
}
