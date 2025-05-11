package com.eroom.attendance.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.eroom.attendance.dto.AnnualLeaveDto;
import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.attendance.repository.AnnualLeaveRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnualLeaveService {
	private final AnnualLeaveRepository annualLeaveRepository;
	private final EmployeeService employeeService;

	public AnnualLeave findByEmployeeNo(Long employeeNo) {
		return annualLeaveRepository.findByEmployee_EmployeeNo(employeeNo);
	}
	// 현재년도 연차 정보 조회
	public AnnualLeave selectAnnualLeaveByEmployeeNoAndYear(Long employeeNo, Long year) {
		return annualLeaveRepository.findByEmployee_EmployeeNoAndYear(employeeNo,year);
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
	
	// 연차 부여(매년 1월1일) 
	public void grantAnnualLeave(Long employeeNo, Long year) {
		// 사원 정보
	    Employee employee = employeeService.findEmployeeByEmployeeNo(employeeNo);
	    if (employee == null) return;	
	    
	    //  퇴사자 제외 처리
	    if (employee.getEmployeeEndDate() != null) return;	    
	    // 입사일 기준 1년 미만이면 연차 부여 제외
	    LocalDate hireDate = employee.getEmployeeHireDate().toLocalDate();
	    LocalDate referenceDate = LocalDate.of(year.intValue(), 5, 12); // 연차 부여 기준일
	    					//	= LocalDate.of(year.intValue(), 1, 1); 
	    if(hireDate.isAfter(referenceDate.minusYears(1))) return; // 입사일이 기준일 기준 1년 이내면 제외
	    
		// 기존 연차 정보가 있는지 확인
		AnnualLeave existing = annualLeaveRepository.findByEmployee_EmployeeNoAndYear(employeeNo, year);
		if(existing != null) {
			return; // 이미 존재하는 경우 생략
		}
		
		// 이전 연차 이월
		Long prevYear = year -1;
		AnnualLeave prevLeave = annualLeaveRepository.findByEmployee_EmployeeNoAndYear(employeeNo, prevYear);
		double carryOver = 0.0;
		if(prevLeave != null) {
			// 총 연차 - 사용 연차 (선사용했을 경우 음수가 나올 수 있음)
			carryOver = prevLeave.getAnnualLeaveTotal() - prevLeave.getAnnualLeaveUsed();
		}
		
		// 연차 계산(기본 15일)
		double total = 15.0 + carryOver;
		
		// 새로운 연차 생성
		AnnualLeave newLeave = AnnualLeave.builder()
							.employee(Employee.builder().employeeNo(employeeNo).build())
							.year(year)
							.annualLeaveTotal(total)
							.annualLeaveUsed(0.0)
							.build();
		annualLeaveRepository.save(newLeave);
		
	}
}
