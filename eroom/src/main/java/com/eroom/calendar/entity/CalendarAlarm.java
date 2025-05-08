package com.eroom.calendar.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="calendar_alarm")
@Builder
@Getter
public class CalendarAlarm {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alarm_id")
	private Long alarmId;
	
	@Column(name = "calendar_no")
	private Long calendarNo;
	
	@Column(name = "employee_no")
	private Long employeeNo;
	
	@Column(name = "alarm_read_yn" )
	private String alarmReadYn = "N";
	
	@Column(name = "alarm_reg_date")
	@CreationTimestamp
	private LocalDateTime alarmRegDate;
	
	@Column(name = "`separator`")
	private String separator;
	
	
	

}
