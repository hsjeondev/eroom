package com.eroom.calendar.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.eroom.calendar.dto.EmployeeCalendarDto;
import com.eroom.calendar.entity.EmployeeCalendar;
import com.eroom.calendar.repository.EmployeeCalendarRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeCalendarService {
	private final EmployeeCalendarRepository repository;
	
	//개인 캘린더 일정 등록
	public EmployeeCalendarDto addEmployeeCalendar(EmployeeCalendarDto dto) {
		EmployeeCalendar param = dto.toEntity();
		EmployeeCalendar result = repository.save(param);
		return new EmployeeCalendarDto().toDto(result);
	}
	
	
	//해당 직원 일정 조회
	public List<EmployeeCalendarDto> getCalendarList(Long employeeNo) {
	    List<EmployeeCalendar> list = repository.findByEmployeeNoAndVisibleYnAndSeparator(employeeNo, "Y", "E001");
	    List<EmployeeCalendarDto> dtoList = new ArrayList<>();

	    for (EmployeeCalendar employeeCalendar : list) {
	        EmployeeCalendarDto dto = new EmployeeCalendarDto();
	        dtoList.add(dto.toDto(employeeCalendar));
	    }

	    return dtoList;
	}
	
	//해당 직원 일정 단일 조회
	public EmployeeCalendarDto findByCalendarNo(Long id) {
        
        EmployeeCalendar calendar = repository.findById(id).orElse(null);
          
        if (calendar == null) {
            return null;
        }      
        EmployeeCalendarDto dto = new EmployeeCalendarDto();
        
       
        return dto.toDto(calendar);
    }
	
	
	//해당 직원 일정 수정
	public EmployeeCalendar updateCalendar(EmployeeCalendarDto param) {
		EmployeeCalendar result = null;
		EmployeeCalendar target = repository.findById(param.getCalendar_no()).orElse(null);
		//target -> calendarNo값이 있으면 이걸 저장해줘
		if(target != null) {
	        param.setCalendar_creator(target.getCalendarCreator());
			result = repository.save(param.toEntity());
		}
		return result;
	}
	
	
	public EmployeeCalendarDto deleteCalendar(Long id) {
	    
	    EmployeeCalendar target = repository.findById(id).orElse(null);
	    if (target == null) {
	        return null;
	    }

	    
	    String changeYtoN = "Y".equals(target.getVisibleYn()) ? "N" : "Y";

	    
	    EmployeeCalendar updated = EmployeeCalendar.builder()
	    	    .calendarNo(target.getCalendarNo())  
	    	    .calendarTitle(target.getCalendarTitle())
	    	    .calendarLocation(target.getCalendarLocation())
	    	    .calendarStartTime(target.getCalendarStartTime())
	    	    .calendarEndTime(target.getCalendarEndTime())
	    	    .calendarContent(target.getCalendarContent())
	    	    .calendarCreator(target.getCalendarCreator())
	    	    .calendarEditor(target.getCalendarEditor())
	    	    .employeeNo(target.getEmployeeNo())
	    	    .separator(target.getSeparator())
	    	    .calendarRegDate(target.getCalendarRegDate())
	    	    .calendarModDate(target.getCalendarModDate())
	    	    .visibleYn(changeYtoN)
	    	    .build();

	    EmployeeCalendar saved = repository.save(updated);
	    return new EmployeeCalendarDto().toDto(saved);
	}
	
	
	
	
	

}
