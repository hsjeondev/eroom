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
	
//	public List<EmployeeCalendarDto> getCalendarList(Long employeeNo) {
//	    List<EmployeeCalendar> list = repository.findByEmployeeEmployeeNo(employeeNo); // ✅ 정확한 메소드명
//	    return list.stream()
//	        .map(entity -> new EmployeeCalendarDto().toDto(entity))
//	        .collect(Collectors.toList());
//	}

}
