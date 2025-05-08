package com.eroom.approval.service;

import org.springframework.stereotype.Service;

import com.eroom.approval.dto.ApprovalAlarmDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalAlarm;
import com.eroom.approval.repository.ApprovalAlarmRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalAlarmService {

	private final ApprovalAlarmRepository approvalAlarmRepository;
	private final EmployeeService employeeservice;
	
	public int alarmSaveMethod(Long employeeNo, String message, Approval endApproval) {
		int result = 0;
		try {
			Employee approvalCreatorEmployee = employeeservice.findEmployeeByEmployeeNo(employeeNo);
			if(approvalCreatorEmployee != null && !message.isEmpty()) {
				ApprovalAlarmDto approvalAlarmDto = ApprovalAlarmDto.builder()
													.approval_alarm_comment(message)
													.approval_alarm_read_yn("N")
													.approval_no(endApproval.getApprovalNo())
													.build();
				ApprovalAlarm approvalAlarmEntity = approvalAlarmDto.toEntity(endApproval, approvalCreatorEmployee);
				approvalAlarmRepository.save(approvalAlarmEntity);
			}
			
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public ApprovalAlarm findAlarmOne(Long param1) {
		return approvalAlarmRepository.findById(param1).orElse(null);
	}

}
