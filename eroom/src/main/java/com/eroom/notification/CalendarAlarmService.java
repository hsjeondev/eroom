package com.eroom.notification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.calendar.entity.CompanyCalendar;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.notification.dto.CalendarAlarmDto;
import com.eroom.notification.entity.CalendarAlarm;
import com.eroom.notification.repository.CalendarAlarmRepository;
import com.eroom.security.EmployeeDetails;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarAlarmService {
	private final CalendarAlarmRepository calendarAlarmRepository;
	private final EmployeeRepository employeeRepository;
	

	@Transactional
	public List<CalendarAlarmDto> getCalendarAlarm() {
	    // 1. 로그인한 사용자 정보에서 employeeNo 가져오기
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    EmployeeDetails user = (EmployeeDetails) auth.getPrincipal(); // 
	    Long employeeNo = user.getEmployeeNo(); // 

	    // 2. DB에서 해당 사용자의 알림 조회
	    List<CalendarAlarm> alarms = calendarAlarmRepository.findByEmployeeNoOrderByAlarmIdDesc(employeeNo);

	    List<CalendarAlarmDto> result = new ArrayList<>();
	    for (CalendarAlarm alarm : alarms) {
	        result.add(new CalendarAlarmDto().toDto(alarm));
	    }

	    return result;
	}

		  
		  @Transactional
		    public void createCompanyCalendarAlarms(CompanyCalendar companyCalendar) {
		        // 1. 전체 직원 조회
		        List<Employee> employeeList = employeeRepository.findAll();

		        // 2. 직원별로 알람 생성
		        for (Employee employee : employeeList) {
		            CalendarAlarm alarm = CalendarAlarm.builder()
		                    .calendarNo(companyCalendar.getCalendarNo()) // 저장된 회사 일정 번호
		                    .employeeNo(employee.getEmployeeNo())        // 직원 번호
		                    .alarmReadYn("N")                             // 안읽음 처리
		                    .separator(companyCalendar.getSeparator())
		                    .build();

		            calendarAlarmRepository.save(alarm);
		        }
		    }
		  
		  @Transactional
		  public List<CalendarAlarmDto> getUnreadCompanyAlarms() {
		      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		      EmployeeDetails user = (EmployeeDetails) auth.getPrincipal();
		      Long employeeNo = user.getEmployeeNo();

		      List<CalendarAlarm> alarms = calendarAlarmRepository.findByEmployeeNoAndAlarmReadYnAndSeparatorStartingWith(
		              employeeNo, "N", "A"
		      );

		      List<CalendarAlarmDto> result = new ArrayList<>();
		      for (CalendarAlarm alarm : alarms) {
		          result.add(new CalendarAlarmDto().toDto(alarm));
		      }

		      return result;
		  }
	}


	

