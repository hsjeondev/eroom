package com.eroom.attendance.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
import lombok.Setter;

@Entity
@Table(name="attendance")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="attendance_no")
	private Long attendanceNo; // 근태 번호
	
	@Column(name="attendance_check_in_time")
	private LocalDateTime attendanceCheckInTime; // 출근 시간

	@Column(name="attendance_check_out_time")
	private LocalDateTime attendanceCheckOutTime; // 퇴근 시간
	
	@Column(name="attendance_late_yn")
	private String attendanceLateYn; // 지각 여부
	
	@Column(name="attendance_early_leave_yn")
	private String attendanceEarlyLeaveYn; // 조퇴 여부
	
	@ManyToOne
	@JoinColumn(name="employee_no")
	private Employee employee; // 사번
	
	

}
