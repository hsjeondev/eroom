package com.eroom.attendance.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.attendance.entity.Attendance;
import com.eroom.attendance.repository.AttendanceRepository;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {
	
	private final AttendanceRepository attendanceRepository;
	
	public List<Attendance> selectAttendanceList(){
		
		// 현재 로그인한 정보
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		
		Long employeeNo = employeeDetail.getEmployee().getEmployeeNo(); 
		
		return null;
//		return attendanceRepository.findById(employeeNo);
		
		
	}

}
