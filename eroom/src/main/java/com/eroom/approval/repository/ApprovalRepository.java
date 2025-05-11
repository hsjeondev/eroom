package com.eroom.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalLine;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

	List<Approval> findByEmployee_EmployeeNoAndApprovalVisibleYnOrderByApprovalRegDateDesc(Long employeeNo, String visible);

	List<Approval> findByApprovalNoAndApprovalVisibleYnOrderByApprovalRegDateDesc(Long approvalNo, String visible);

	List<Approval> findAllByApprovalVisibleYn(String string);

	// mainpage Approval 리스트 조회1
	List<Approval> findByEmployee_EmployeeNoAndApprovalStatusAndApprovalVisibleYnOrderByApprovalRegDateDesc(Long employeeNo,
			String approvalStatus, String approvalVisibleYn);
	// 마이페이지용 결재 불러오기
	List<Approval> findTop5ByApprovalStatusAndApprovalVisibleYnAndEmployee_EmployeeNoOrderByApprovalRegDateDesc(String approvalStatus, String approvalVisibleYn, Long employeeNo);
	// mainpage Approval 리스트 조회3
	List<Approval> findTop5ByApprovalStatusInAndApprovalVisibleYnAndEmployee_EmployeeNoOrderByApprovalRegDateDesc(
		    List<String> approvalStatuses, String approvalVisibleYn, Long employeeNo
		);




	
}
