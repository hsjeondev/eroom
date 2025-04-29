package com.eroom.approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.approval.entity.ApprovalAlarm;
import com.eroom.approval.entity.ApprovalSignature;
import com.eroom.employee.entity.Employee;

public interface ApprovalSignatureRepository extends JpaRepository<ApprovalSignature, Long> {

	ApprovalSignature findByEmployee(Employee employee);

}
