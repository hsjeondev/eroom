package com.eroom.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.approval.entity.ApprovalLine;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long> {

//	@Query("SELECT DISTINCT a FROM ApprovalLine a WHERE a.approval.approvalNo = :approvalNo ORDER BY a.approvalLineNo")
//	List<ApprovalLine> findByApproval_ApprovalNoOrderByApprovalLineNoAsc(@Param("approvalNo") Long approvalNo);
	@Query("SELECT DISTINCT a FROM ApprovalLine a JOIN FETCH a.employee WHERE a.approval.approvalNo = :approvalNo")
	List<ApprovalLine> findApprovalLines(@Param("approvalNo") Long approvalNo);


	
//	List<ApprovalLine> findByApproval_ApprovalNoOrderByApprovalLineNoAsc(Long approvalNo);


}
