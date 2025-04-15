package com.eroom.calendar.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.eroom.calendar.dto.EmployeeCalendarDto;
import com.eroom.calendar.entity.EmployeeCalendar;
import com.eroom.calendar.repository.EmployeeCalendarRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeCalendarService {
	private final EmployeeCalendarRepository repository;
	
	public EmployeeCalendarDto addEmployeeCalendar(EmployeeCalendarDto dto) {
		EmployeeCalendar param = dto.toEntity();
		EmployeeCalendar result = repository.save(param);
		return new EmployeeCalendarDto().toDto(result);
	}
	
	public List<EmployeeCalendarDto> getCalendarList(Long employeeNo) {
	    List<EmployeeCalendar> list = repository.findByEmployeeNo(employeeNo);
	    return list.stream()
	        .map(employeeCalendar -> {
	            EmployeeCalendarDto dto = new EmployeeCalendarDto();
	            return dto.toDto(employeeCalendar); // EmployeeCalendar를 EmployeeCalendarDto로 변환
	        })
	        .collect(Collectors.toList());
	}
	
	//단일 조회
	public EmployeeCalendarDto findByCalendarNo(Long id) {
        // 데이터베이스에서 일정 가져오기
        EmployeeCalendar calendar = repository.findById(id).orElse(null);
        
        // 데이터가 없으면 null 반환
        if (calendar == null) {
            return null;
        }

        // EmployeeCalendarDto 객체 생성
        EmployeeCalendarDto dto = new EmployeeCalendarDto();
        
        // toDto 호출
        return dto.toDto(calendar);
    }
	
	public EmployeeCalendar updateCalendar(EmployeeCalendarDto param) {
		EmployeeCalendar result = null;
		EmployeeCalendar target = repository.findById(param.getCalendar_no()).orElse(null);
		//target -> calendarNo값이 있으면 이걸 저장해줘
		if(target != null) {
			result = repository.save(param.toEntity());
		}
		return result;
	}
	
	public EmployeeCalendarDto deleteCalendar(Long id) {
	    EmployeeCalendar target = repository.findById(id).orElse(null);
	    if (target == null) {
	        return null;
	    }

	    EmployeeCalendarDto dto = new EmployeeCalendarDto().toDto(target);

	    if ("Y".equals(dto.getVisibleYn())) {
	        dto.setVisibleYn(null);
	    } else {
	        dto.setVisibleYn("Y");
	    }

	    EmployeeCalendar updated = dto.toEntity();
	    EmployeeCalendar saved = repository.save(updated);
	    return new EmployeeCalendarDto().toDto(saved);
	}
	
	
	

}
