package com.eroom.calendar.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.calendar.dto.CompanyCalendarDto;
import com.eroom.calendar.dto.TeamCalendarDto;
import com.eroom.calendar.entity.CompanyCalendar;
import com.eroom.calendar.entity.TeamCalendar;
import com.eroom.calendar.repository.TeamCalendarRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamCalendarService {
	private final TeamCalendarRepository teamRepository;
	
	//나의 팀 일정 등록
	public TeamCalendarDto addTeamCalendar(TeamCalendarDto dto) {
		TeamCalendar param = dto.toEntity();
		TeamCalendar result =  teamRepository.save(param);
		return new TeamCalendarDto().toDto(result);
	}
	
	//나의 팀 일정 목록 조회
	public List<TeamCalendarDto> getTeamList(String separator){
		  List<TeamCalendar> list = teamRepository.findBySeparatorAndVisibleYn(separator,"Y");
		    List<TeamCalendarDto> dtoList = new ArrayList<>();

		    for (TeamCalendar teamCalendar : list) {
		        TeamCalendarDto dto = new TeamCalendarDto();
		        dtoList.add(dto.toDto(teamCalendar));
		    }

		    return dtoList;
	}
	
	//나의 팀 일정 단일 조회
	public TeamCalendarDto findByTeamNo(Long id) {
		  TeamCalendar calendar = teamRepository.findById(id).orElse(null);
	        
	        // 데이터가 없으면 null 반환
	        if (calendar == null) {
	            return null;
	        }

	        // EmployeeCalendarDto 객체 생성
	        TeamCalendarDto dto = new TeamCalendarDto();
	        
	        // toDto 호출
	        return dto.toDto(calendar);
	    }
	
	//나의 팀 일정 수정
	public TeamCalendar updateTeamCalendar(TeamCalendarDto param) {
		TeamCalendar result = null;
		TeamCalendar target = teamRepository.findById(param.getCalendar_no()).orElse(null);
		if(target != null ) {
			param.setTeam_creator(target.getTeamCreator());
			param.setSeparator(target.getSeparator());
			result = teamRepository.save(param.toEntity());
		}
		return result;
	}
	
	//나의 팀 일정 삭제
	public TeamCalendarDto deleteTeamCalendar(Long id) {
		TeamCalendar target = teamRepository.findById(id).orElse(null);
		if(target == null) {
			return null;
		}
		
		 String changeYtoN = "Y".equals(target.getVisibleYn()) ? "N" : "Y";
	    
		 	TeamCalendar updated = TeamCalendar.builder()
	    	    .calendarNo(target.getCalendarNo())  
	    	    .teamTitle(target.getTeamTitle())
	    	    .teamLocation(target.getTeamLocation())
	    	    .calendarStartTime(target.getCalendarStartTime())
	    	    .calendarEndTime(target.getCalendarEndTime())
	    	    .teamContent(target.getTeamContent())
	    	    .teamCreator(target.getTeamCreator())
	    	    .teamEditor(target.getTeamEditor())
	    	    .employeeNo(target.getEmployeeNo())
	    	    .separator(target.getSeparator())
	    	    .calendarRegDate(target.getCalendarRegDate())
	    	    .calendarModDate(target.getCalendarModDate())
	    	    .visibleYn(changeYtoN)
	    	    .build();
	    TeamCalendar saved = teamRepository.save(updated);
	    return new TeamCalendarDto().toDto(saved);
	}
	


}
