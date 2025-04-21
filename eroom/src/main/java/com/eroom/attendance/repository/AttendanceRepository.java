package com.eroom.attendance.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>{
	
	List<Attendance> findAll(Specification<Attendance> spec);
	// 전체 근태 기록 조회
	List<Attendance> findByEmployee_EmployeeNoOrderByAttendanceCheckInTimeDesc(Long employeeNo);
	
	// 오늘 출근 기록 중 마지막 출근 기록 조회
	@Query("SELECT a FROM Attendance a WHERE a.employee.employeeNo = :employeeNo AND a.attendanceCheckInTime BETWEEN :start AND :end ORDER BY a.attendanceNo DESC")
	Attendance findLastCheckInToday(@Param("employeeNo") Long employeeNo,
									@Param("start") LocalDateTime start,
									@Param("end") LocalDateTime end);
	
	// 오늘 출근 기록 전체 조회
	@Query("SELECT a FROM Attendance a WHERE a.employee.employeeNo = :employeeNo AND a.attendanceCheckInTime BETWEEN :start AND :end ORDER BY a.attendanceNo DESC")
	List<Attendance> findTodayAttendanceList(@Param("employeeNo") Long employeeNo, @Param("start") LocalDateTime start,@Param("end") LocalDateTime end);

	
	// 월별 출근 기록 조회
	List<Attendance> findByEmployee_EmployeeNoAndAttendanceCheckInTimeBetweenOrderByAttendanceCheckInTimeDesc(Long employeeNo, LocalDateTime start, LocalDateTime end);
	
	// 근태 기록이 있는 월 목록(중복X)
	@Query("SELECT DISTINCT DATE_FORMAT(a.attendanceCheckInTime, '%Y.%m') AS formattedMonth FROM Attendance a WHERE a.employee.employeeNo = :employeeNo ORDER BY formattedMonth DESC")
    List<String> findDistinctAttendanceMonth(@Param("employeeNo") Long employeeNo);

}
