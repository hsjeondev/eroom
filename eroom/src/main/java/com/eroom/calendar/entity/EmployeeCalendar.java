package com.eroom.calendar.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="employee_calendar")
@Builder
@Getter
public class EmployeeCalendar {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="calendar_no")
	private Long calendarNo;
	

    @Column(name = "calendar_start")
    private LocalDateTime calendarStart;
    
    @Column(name = "calendar_end")
    private LocalDateTime calendarEnd;
    
    @Column(name = "calendar_title")
    private String calendarTitle;
	
	@Column(name="calendar_location")
	private String calendarLocation;
	
	@Column(name="calendar_description")
	private String calendarDescription;
	
	@CreationTimestamp
	@Column(updatable=false,name="calendar_reg_date")
	private LocalDateTime calendarRegDate;
	
	@UpdateTimestamp
	@Column(insertable=false,name="calendar_mod_date")
	private LocalDateTime calendarModDate;
	
	@ManyToOne
	@JoinColumn(name="employee_no")
	private Employee employee;
	

}
