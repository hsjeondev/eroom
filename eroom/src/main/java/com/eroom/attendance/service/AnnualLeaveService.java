package com.eroom.attendance.service;

import org.springframework.stereotype.Service;

import com.eroom.attendance.dto.AnnualLeaveDto;
import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.attendance.repository.AnnualLeaveRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnualLeaveService {
	private final AnnualLeaveRepository annualLeaveRepository;

	public AnnualLeave findByEmployeeNo(Long employeeNo) {
		return annualLeaveRepository.findByEmployee_EmployeeNo(employeeNo);
	}
	
	// 연차 정보 수정
	public AnnualLeaveDto updateAnnualLeave(Long employeeNo, double totalDelta, double usedDelta) {
		AnnualLeave entity = annualLeaveRepository.findByEmployee_EmployeeNo(employeeNo);
		if(entity != null) {
			double newTotal = entity.getAnnualLeaveTotal() + totalDelta;
			double newUsed = entity.getAnnualLeaveUsed() + usedDelta;
			double newRemain = newTotal - newUsed;
			
			entity.setAnnualLeaveTotal(newTotal);
			entity.setAnnualLeaveUsed(newUsed);
			annualLeaveRepository.save(entity);
			return AnnualLeaveDto.toDto(entity);
		}
		return null;
	}
	
}
