package com.eroom.attendance.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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

@Entity
@Table(name="attendance")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="attendance_no")
	private Long attendanceNo;
	
	@CreationTimestamp
	@Column(updatable=false,name="attendance_check_in_time")
	private LocalDateTime attendanceCheckInTime;

	@CreationTimestamp
	@Column(updatable=false,name="attendance_check_out_time")
	private LocalDateTime attendanceCheckOutTime;
	
	@Column(name="attendance_late_yn")
	private String attendanceLateYn;
	
	@Column(name="attendance_early_leave_yn")
	private String attendanceEarlyLeaveYn;
	
//	@ManyToOne
//	@JoinColumn(name="employee_no")
//	private Employee employee;
	
	

}
