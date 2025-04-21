package com.eroom.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.approval.entity.ApprovalLine;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long> {

	List<ApprovalLine> findByApproval_ApprovalNoOrderByApprovalLineNoAsc(Long approvalNo);
}
