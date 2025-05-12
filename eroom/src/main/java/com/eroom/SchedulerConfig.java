package com.eroom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.eroom.attendance.service.AnnualLeaveService;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;
import com.eroom.mail.repository.MailStatusRepository;
import com.eroom.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
	
	private final ProjectRepository projectRepository;
	private final MailStatusRepository mailStatusRepository;
	private final EmployeeService employeeService;
	private final AnnualLeaveService annualLeaveService;
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void updateProjectProceed() {
		
		LocalDate yesterday = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
		
		projectRepository.updateProceedByEndDate("완료", yesterday);
		
	}
	// 메일 휴지통
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") 
	public void deleteOldMailTrash() {
	    LocalDateTime limitDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(15);
	    mailStatusRepository.updateVisibilityOfOldTrash(limitDate);
	}
	@Scheduled(cron = "0 0 0 1 1 *", zone = "Asia/Seoul")
	public void runEveryYear() {
	    // System.out.println("새해 첫날 실행됨!");
	}
	@Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
	public void runEveryMonth() {
	    // System.out.println("매달 1일 00시에 실행됨!");
	}
	// 연차부여
	@Scheduled(cron = "0 13 1 12 5 *", zone = "Asia/Seoul") // 테스트용 : 5월 12일 
	public void grantAnnualLeaveToAllEmployees() {
		// 현재 연도 가져오기
		Long currentYear = Long.valueOf(LocalDate.now(ZoneId.of("Asia/Seoul")).getYear());
		// 전체 사원 목록 조회
		List<Employee> employeeList = employeeService.findAllEmployee();
		for(Employee employee : employeeList) {
			Long employeeNo = employee.getEmployeeNo();
			annualLeaveService.grantAnnualLeave(employeeNo, currentYear);
		}
		System.out.println("[연차 스케줄러] "+currentYear + "년도 연차가 부여되었습니다.");
	}
 }
