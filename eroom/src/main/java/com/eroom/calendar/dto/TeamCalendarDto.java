package com.eroom.calendar.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.eroom.calendar.entity.TeamCalendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class TeamCalendarDto {
	private Long calendar_no;
	private String team_title;
	private String team_location;
	private LocalDateTime calendar_start_time; // 시작 시간
	private LocalDateTime calendar_end_time; // 종료 시간
	private String team_content; // 설명
	private String team_creator;
	private String team_editor;
	private Long employee_no; // 사번
	private String separator; // 구분자
	private String visibleYn = "Y";
	private LocalDateTime calendar_reg_date;
	private LocalDateTime calendar_mod_date;
	
	public TeamCalendar toEntity() {
		return TeamCalendar.builder()
				.employeeNo(employee_no)
				.calendarNo(calendar_no)
				.teamTitle(team_title)
				.teamLocation(team_location)
				.calendarStartTime(calendar_start_time)
				.calendarEndTime(calendar_end_time)
				.teamContent(team_content)
				.teamCreator(team_creator)
				.teamEditor(team_editor)
				.separator(separator)
				.visibleYn(visibleYn)
				.build();
	}
	
	public TeamCalendarDto toDto(TeamCalendar teamCalendar) {
		return TeamCalendarDto.builder().calendar_no(teamCalendar.getCalendarNo())
				.team_title(teamCalendar.getTeamTitle())
				.team_location(teamCalendar.getTeamLocation())
				.calendar_start_time(teamCalendar.getCalendarStartTime())
				.calendar_end_time(teamCalendar.getCalendarEndTime())
				.team_content(teamCalendar.getTeamContent())
				.team_creator(teamCalendar.getTeamCreator())
				.team_editor(teamCalendar.getTeamEditor())
				.employee_no(teamCalendar.getEmployeeNo())
				.separator(teamCalendar.getSeparator())
				.calendar_reg_date(teamCalendar.getCalendarRegDate())
				.calendar_mod_date(teamCalendar.getCalendarModDate())
				.employee_no(teamCalendar.getEmployeeNo())
				.build();
	}
	
	public Map<String, Object> toFullCalendarEvent() {
	    Map<String, Object> event = new HashMap<>();
	    event.put("title", this.team_title != null ? this.team_title : "제목 없음");
	    event.put("start", this.calendar_start_time != null ? this.calendar_start_time.toString() : "");
	    event.put("end", this.calendar_end_time != null ? this.calendar_end_time.toString() : "");
	    event.put("location", this.team_location != null ? this.team_location : "");
	    event.put("description", this.team_content != null ? this.team_content : "");
	    event.put("separator", this.separator != null ? this.separator : "");
	    event.put("calendar_creator", this.team_creator != null ? this.team_creator : "");
	    event.put("calendar_no", this.calendar_no);
	    

	    
	    
	    return event;
	}

}
