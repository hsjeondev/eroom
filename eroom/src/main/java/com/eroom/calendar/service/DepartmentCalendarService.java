package com.eroom.calendar.service;

import java.util.ArrayList;
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

        System.out.println("[부서 코드] " + departmentCode);

        List<String> teamCodes = structureRepository.findTeamCodesByDepartment(departmentCode);
        System.out.println("[팀 코드 목록] " + teamCodes);

        List<DepartmentCalendar> calendarList = calendarRepository.findBySeparatorInAndVisibleYn(teamCodes, "Y");
        System.out.println("[조회된 일정 수] " + calendarList.size());

        for (DepartmentCalendar cal : calendarList) {
            System.out.println("일정 제목: " + cal.getCalendarTitle());
            System.out.println("시작 시간: " + cal.getCalendarStartTime());
            System.out.println("팀 이름: " + cal.getEmployee().getStructure().getCodeName());
        }

        List<DepartmentCalendarDto> result = new ArrayList<>();
        for (DepartmentCalendar cal : calendarList) {
            DepartmentCalendarDto dto = new DepartmentCalendarDto().toDto(cal);
            result.add(dto);
        }

        return result;
    }
}

	


