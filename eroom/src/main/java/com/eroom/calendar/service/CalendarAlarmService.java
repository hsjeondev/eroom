package com.eroom.calendar.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.calendar.dto.CalendarAlarmDto;
import com.eroom.calendar.entity.CalendarAlarm;
import com.eroom.calendar.entity.CompanyCalendar;
import com.eroom.calendar.entity.TeamCalendar;
import com.eroom.calendar.repository.CalendarAlarmRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.security.EmployeeDetails;
import com.eroom.websocket.TeamAlarmSocketHandler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarAlarmService {
	private final CalendarAlarmRepository calendarAlarmRepository;
	private final EmployeeRepository employeeRepository;
	private final TeamAlarmSocketHandler teamAlarmSocketHandler;
	  		
          //회사 일정 등록 시 전체 직원에게 알림 저장
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
		  
		  //해당 팀원에게 알림 저장
		  @Transactional
		  public void createTeamCalendarAlarms(TeamCalendar calendar) {
			  List<Employee> teamMembers = employeeRepository.findByStructure_SeparatorCode(calendar.getSeparator());

			    List<CalendarAlarm> alarms = new ArrayList<>();
			    for (Employee member : teamMembers) {
			        CalendarAlarm alarm = CalendarAlarm.builder()
			            .calendarNo(calendar.getCalendarNo())
			            .employeeNo(member.getEmployeeNo())
			            .alarmReadYn("N")
			            .separator(calendar.getSeparator())
			            .alarmRegDate(LocalDateTime.now())
			            .build();
			        alarms.add(alarm);
			    }

			    calendarAlarmRepository.saveAll(alarms); // 저장
			    teamAlarmSocketHandler.broadcastToTeam(calendar.getSeparator(), "new"); // 팀 웹소켓 알림
			}
		}

//=======================================================================================================
		  
		  //알림 페이지용 전체 알림 조회 (Y/N 모두)
//		  @Transactional
//		  public List<CalendarAlarmDto> getMyAlarms() {
//		      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		      EmployeeDetails user = (EmployeeDetails) auth.getPrincipal();
//		      Long employeeNo = user.getEmployeeNo();
//
//		      List<CalendarAlarm> alarms = calendarAlarmRepository.findByEmployeeNoOrderByAlarmRegDateDesc(employeeNo);
//
//
//		      List<CalendarAlarmDto> result = new ArrayList<>();
//		      for (CalendarAlarm alarm : alarms) {
//		          result.add(new CalendarAlarmDto().toDto(alarm));
//		      }
//		      return result;
//		  }
		  //헤더 종에서 N인 알림만 보여줄 때 사용
//		  @Transactional
//		  public List<CalendarAlarmDto> getUnreadAlarms() {
//		      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		      EmployeeDetails user = (EmployeeDetails) auth.getPrincipal();
//		      Long employeeNo = user.getEmployeeNo();
//
//		      // 🔽 N인 것만 조회
//		      List<CalendarAlarm> alarms = calendarAlarmRepository
//		    	        .findByEmployeeNoAndAlarmReadYnOrderByAlarmRegDateDesc(employeeNo, "N");
//
//
//		      List<CalendarAlarmDto> result = new ArrayList<>();
//		      for (CalendarAlarm alarm : alarms) {
//		          result.add(new CalendarAlarmDto().toDto(alarm));
//		      }
//		      return result;
//		  }
		  		  
		  //목록에서 회사 일정 클릭하면 캘린더로 이동 -> 그리고 N을 Y로 변경
//		  @Transactional
//		  public void markAsRead(Long alarmId) {
//		      CalendarAlarm target = calendarAlarmRepository.findById(alarmId).orElse(null);
//
//		      if (target != null && "N".equals(target.getAlarmReadYn())) {
//		          CalendarAlarm updated = CalendarAlarm.builder()
//		              .alarmId(target.getAlarmId())
//		              .calendarNo(target.getCalendarNo())
//		              .employeeNo(target.getEmployeeNo())
//		              .separator(target.getSeparator())
//		              .alarmReadYn("Y") // 읽음 처리
//		              .alarmRegDate(target.getAlarmRegDate())
//		              .build();
//
//		          calendarAlarmRepository.save(updated);
//		      }
//		  }
		  
		  //전체 읽음 처리 버튼
//		  @Transactional
//		  public void markAllAsRead(Long employeeNo) {
//		      calendarAlarmRepository.updateAllToReadByEmployeeNo(employeeNo);
//		  }
//		  
		  
	

	
	


	

