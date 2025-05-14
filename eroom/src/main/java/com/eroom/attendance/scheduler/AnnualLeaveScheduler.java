package com.eroom.attendance.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eroom.attendance.service.AnnualLeaveService;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

/**
 * 연차 자동 부여 스케줄러 클래스
 * 매년 1월1일 (테스트 날짜 5월12일 또는 13일) 에 모든 사원에게 연차 자동부여한다.	
 */
@Component
@RequiredArgsConstructor
public class AnnualLeaveScheduler {
	
	private final EmployeeService employeeService;
	private final AnnualLeaveService annualLeaveService;
	/**
	 *  매년 1월 1일 00시에 실행 (테스트용 : 매일 자정으로 설정)
	 */
	@Scheduled(cron = "0 17 10 14 5 *", zone= "Asia/Seoul") // 테스트용 : 5월 12일 00시
	public void generateAnnualLeaves() {
		// 오늘 날짜 기준 연도
		int yearInt = LocalDate.now().getYear();
		Long year = Long.valueOf(yearInt);
		// 전체 재직중 사원 목록
		List<Employee> employeeList = employeeService.findAllEmployee();
		
		for(Employee employee : employeeList) {
			// 연차 부여 서비스 호출
			annualLeaveService.grantAnnualLeave(employee.getEmployeeNo(),year);
		}
	}
}
