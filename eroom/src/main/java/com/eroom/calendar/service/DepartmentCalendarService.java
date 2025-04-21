package com.eroom.calendar.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.eroom.calendar.dto.DepartmentCalendarDto;
import com.eroom.calendar.entity.DepartmentCalendar;
import com.eroom.calendar.repository.DepartmentCalendarRepository;
import com.eroom.employee.repository.StructureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentCalendarService {
	

    private final StructureRepository structureRepository;
    private final DepartmentCalendarRepository calendarRepository;
	
    public List<DepartmentCalendarDto> getDepartmentCalendar(String departmentCode) {

        //System.out.println("[부서 코드] " + departmentCode);

        List<String> teamCodes = structureRepository.findTeamCodesByDepartment(departmentCode);
        //System.out.println("[팀 코드 목록] " + teamCodes);

        List<DepartmentCalendar> calendarList = calendarRepository.findBySeparatorInAndVisibleYn(teamCodes, "Y");
        //System.out.println("[조회된 일정 수] " + calendarList.size());

        for (DepartmentCalendar cal : calendarList) {
            //System.out.println("일정 제목: " + cal.getCalendarTitle());
            //System.out.println("시작 시간: " + cal.getCalendarStartTime());
            //System.out.println("팀 이름: " + cal.getEmployee().getStructure().getCodeName());
        }

        List<DepartmentCalendarDto> result = new ArrayList<>();
        for (DepartmentCalendar cal : calendarList) {
            DepartmentCalendarDto dto = new DepartmentCalendarDto().toDto(cal);
            result.add(dto);
        }

        return result;
    }
    
    public List<Map<String, Object>> getAllVisibleCalendars() {
        List<DepartmentCalendar> list = calendarRepository.findBySeparatorStartingWithAndVisibleYn("T", "Y");

        List<Map<String, Object>> result = new ArrayList<>();
        for (DepartmentCalendar entity : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("calendar_no", entity.getCalendarNo());
            map.put("title", entity.getCalendarTitle());
            map.put("start", entity.getCalendarStartTime().toString());
            map.put("end", entity.getCalendarEndTime().toString());
            map.put("location", entity.getCalendarLocation());
            map.put("description", entity.getCalendarContent());
            map.put("separator", entity.getSeparator());
            result.add(map);
        }
        return result;
    }
}

	


