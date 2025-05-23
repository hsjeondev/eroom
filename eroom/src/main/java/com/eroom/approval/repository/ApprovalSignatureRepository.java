package com.eroom.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.approval.entity.ApprovalAlarm;
import com.eroom.approval.entity.ApprovalSignature;
import com.eroom.employee.entity.Employee;

public interface ApprovalSignatureRepository extends JpaRepository<ApprovalSignature, Long> {


	List<ApprovalSignature> findByEmployeeAndApprovalSignatureVisibleYn(Employee employee, String string);

	List<ApprovalSignature> findByEmployee_EmployeeNoAndApprovalSignatureVisibleYn(Long employeeNo, String string);



}
