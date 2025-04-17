package com.eroom.attendance.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>{
	
	List<Attendance> findAll(Specification<Attendance> spec);
	
	List<Attendance> findByEmployee_EmployeeNo(Long employeeNo);
	
	// 마지막 출근 기록 조회
	@Query("SELECT a FROM Attendance a WHERE a.employee.employeeNo = :employeeNo AND a.attendanceCheckInTime BETWEEN :start AND :end ORDER BY a.attendanceNo DESC")
	Attendance findLastCheckInToday(@Param("employeeNo") Long employeeNo,
									@Param("start") LocalDateTime start,
									@Param("end") LocalDateTime end);
	
	// 출근 기록 조회
	@Query("SELECT a FROM Attendance a WHERE a.employee.employeeNo = :employeeNo AND a.attendanceCheckInTime BETWEEN :start AND :end ORDER BY a.attendanceNo DESC")
	List<Attendance> findTodayAttendanceList(@Param("employeeNo") Long employeeNo, @Param("start") LocalDateTime start,@Param("end") LocalDateTime end);

}
