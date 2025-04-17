package com.eroom.attendance.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.attendance.dto.AttendanceDto;
import com.eroom.attendance.entity.Attendance;
import com.eroom.attendance.repository.AttendanceRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
@Setter
public class AttendanceService {
	
	private final AttendanceRepository attendanceRepository;
	
	// 출퇴근 기록
	public Attendance recordAttendance(AttendanceDto dto){
		
		LocalDateTime now = LocalDateTime.now();
		
		// 로그인 인증 정보
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		Long employeeNo = employeeDetail.getEmployee().getEmployeeNo();
		
		// 지각 조퇴 여부 default 값
		String lateYn = "N";
		String earlyLeaveYn = "N";
		
		// 오늘 출근한 기록
		// toLocalDate() -> 날짜만 꺼냄, atStartOfDay() -> 그 날짜의 자정(00:00)
		LocalDateTime todayStart = now.toLocalDate().atStartOfDay(); 
		// 오늘 + 1일 자정(다음날 00시) 
		LocalDateTime todayEnd = todayStart.plusDays(1);
		// 오늘 출근 기록 조회
		Attendance todayAttendance = attendanceRepository.findLastCheckInToday(employeeNo, todayStart, todayEnd);
		// 타입이 출근일때
		if("checkIn".equals(dto.getAttendanceType())) {
			// 이미 출근 한 경우
			if(todayAttendance != null) return null;
			
			// 지각 체크
			if(now.getHour() > 9 || (now.getHour() == 9 && now.getMinute() > 0)) lateYn = "Y";
			
			Attendance attendance = Attendance.builder()
						.employee(Employee.builder().employeeNo(employeeNo).build())
						.attendanceCheckInTime(now)
						.attendanceLateYn(lateYn)
						.build();
			return	attendanceRepository.save(attendance);
			
		}else if("checkOut".equals(dto.getAttendanceType())) { // 퇴근일때

			// 출근 기록 없으면 퇴근 X
			if(todayAttendance == null) return null;
			
			// 퇴근 한 경우
			if(todayAttendance.getAttendanceCheckOutTime() != null) return null;
			// 18 시 이전이면 조퇴
			if(now.getHour() < 18) earlyLeaveYn = "Y";
			
			// 기존 출근 기록에 퇴근 정보 추가
			todayAttendance.setAttendanceCheckOutTime(now);
			todayAttendance.setAttendanceEarlyLeaveYn(earlyLeaveYn);
			
			return attendanceRepository.save(todayAttendance);
					

		}
		
		return null;
	}
	
	
	public List<Attendance> selectAttendanceList(){
		
		// 현재 로그인한 정보
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		
		Long employeeNo = employeeDetail.getEmployee().getEmployeeNo(); 
		
		return null;
//		return attendanceRepository.findById(employeeNo);
		
		
	}
	
	
	

}
