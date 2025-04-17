package com.eroom.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.approval.entity.Approval;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

	List<Approval> findByEmployee_EmployeeNo(Long employeeNo);
	
}
