package com.eroom.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.attendance.entity.AnnualLeave;

public interface AnnualLeaveRepository extends JpaRepository<AnnualLeave, Long> {

	// 연차 정보 조회
	AnnualLeave findByEmployee_EmployeeNo(Long employeeNo);

	// 연차 정보 저장
	AnnualLeave save(AnnualLeave annualLeave);



}
