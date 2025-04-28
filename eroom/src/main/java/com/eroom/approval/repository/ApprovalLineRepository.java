package com.eroom.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.approval.entity.ApprovalLine;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long> {

	// 결재 번호로 결재 라인들(ApprovalLine)과 이에 속하는 employee 정보 조회
	@Query("SELECT DISTINCT a FROM ApprovalLine a JOIN FETCH a.employee WHERE a.approval.approvalNo = :approvalNo")
	List<ApprovalLine> findApprovalLines(@Param("approvalNo") Long approvalNo);

	// 회원 번호로 결재 라인 조회
	List<ApprovalLine> findApprovalLinesByEmployee_EmployeeNo(Long employeeNo);
	
	ApprovalLine findByApproval_ApprovalNoAndEmployee_EmployeeNo(Long approvalNo, Long employeeNo);
	
	@Query("SELECT a FROM ApprovalLine a JOIN FETCH a.employee WHERE a.approval.approvalNo = :approvalNo AND a.approvalLineStep >= 1")
	List<ApprovalLine> findApprovalLinesByStepGreaterThanEqualOne(@Param("approvalNo") Long approvalNo);
	
	@Query("SELECT a FROM ApprovalLine a JOIN FETCH a.employee WHERE a.approval.approvalNo = :approvalNo AND a.approvalLineStep = 0")
	List<ApprovalLine> findApprovalLinesByStepEqualZero(@Param("approvalNo") Long approvalNo);


}
