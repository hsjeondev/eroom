package com.eroom.approval.service;

import java.util.Base64;

import org.springframework.stereotype.Service;

import com.eroom.approval.dto.ApprovalSignatureDto;
import com.eroom.approval.entity.ApprovalSignature;
import com.eroom.approval.repository.ApprovalSignatureRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalSignatureService {
	
	private final ApprovalSignatureRepository approvalSignatureRepository;
	private final EmployeeService employeeService;
 
	public int createSignature(ApprovalSignatureDto dto) {
		int result = 0;
		try {
			Employee employee = employeeService.findEmployeeByEmployeeNo(dto.getEmployee_no());
			ApprovalSignature entity = approvalSignatureRepository.findByEmployee(employee);
			if(entity == null) {
				ApprovalSignature createEntity = dto.toEntity();
				approvalSignatureRepository.save(createEntity);
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String encodeToBase64(byte[] signatureBlob) {
		return Base64.getEncoder().encodeToString(signatureBlob);
	}

	public ApprovalSignature findMySignature(Employee employee) {
		return approvalSignatureRepository.findByEmployee(employee);
	}

	public int updateSignature(ApprovalSignatureDto dto) {
		int result = 0;
		try {
			Employee employee = employeeService.findEmployeeByEmployeeNo(dto.getEmployee_no());
			ApprovalSignature entity = approvalSignatureRepository.findByEmployee(employee);
			
			
		    byte[] blob = null;
		    if (dto.getSignature_data_url() != null && dto.getSignature_data_url().contains(",")) {
		      blob = Base64.getDecoder().decode(dto.getSignature_data_url().split(",", 2)[1]);
		    }
			
			if(entity != null) {
				entity.setApprovalSignatureBlob(blob);
				approvalSignatureRepository.save(entity);
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	

}
