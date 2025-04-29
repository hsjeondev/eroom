package com.eroom.approval.service;

import java.util.Base64;
import java.util.List;

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
	
	// 서명 등록
	public int createSignature(ApprovalSignatureDto dto) {
		int result = 0;
		try {
			Employee employee = employeeService.findEmployeeByEmployeeNo(dto.getEmployee_no());
			List<ApprovalSignature> entityList = approvalSignatureRepository.findByEmployeeAndApprovalSignatureVisibleYn(employee, "Y");
			if(entityList.isEmpty()) {
				dto.setApproval_signature_visible_yn("Y");
				ApprovalSignature createEntity = dto.toEntity();
				approvalSignatureRepository.save(createEntity);
				result = 1;
			} else {
				byte[] blob = null;
			    if (dto.getSignature_data_url() != null && dto.getSignature_data_url().contains(",")) {
			      blob = Base64.getDecoder().decode(dto.getSignature_data_url().split(",", 2)[1]);
			    }
				entityList.get(entityList.size() - 1).setApprovalSignatureBlob(blob);
				approvalSignatureRepository.save(entityList.get(entityList.size() - 1));
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	// 서명 수정
//	public int updateSignature(ApprovalSignatureDto dto) {
//		int result = 0;
//		try {
//			Employee employee = employeeService.findEmployeeByEmployeeNo(dto.getEmployee_no());
//			List<ApprovalSignature> entityList = approvalSignatureRepository.findByEmployeeAndApprovalSignatureVisibleYn(employee, "Y");
//			
//			
//		    byte[] blob = null;
//		    if (dto.getSignature_data_url() != null && dto.getSignature_data_url().contains(",")) {
//		      blob = Base64.getDecoder().decode(dto.getSignature_data_url().split(",", 2)[1]);
//		    }
//			
//			if(entityList.size() == 1) {
//				
//				entityList.get(entityList.size() - 1).setApprovalSignatureBlob(blob);
//				approvalSignatureRepository.save(entityList.get(entityList.size() - 1));
//				result = 1;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
	
	// 서명 삭제
	public int deleteSignature(Long employeeNo) {
		int result = 0;
		try {
			Employee employee = employeeService.findEmployeeByEmployeeNo(employeeNo);
			List<ApprovalSignature> entityList = approvalSignatureRepository.findByEmployeeAndApprovalSignatureVisibleYn(employee, "Y");
			
			
			if(entityList.size() >= 1) {
				for(ApprovalSignature l : entityList) {
					l.setApprovalSignatureVisibleYn("N");
					approvalSignatureRepository.save(l);
				}
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	
	
	
	
	// 바이너리 데이터 -> base64 데이터
	public String encodeToBase64(byte[] signatureBlob) {
		return Base64.getEncoder().encodeToString(signatureBlob);
	}
	
	// 내 서명 찾기
	public ApprovalSignature findMySignature(Employee employee) {
		List<ApprovalSignature> list =approvalSignatureRepository.findByEmployeeAndApprovalSignatureVisibleYn(employee, "Y");
		if(list.isEmpty()){
			return null;
		} else {
			return list.get(list.size() - 1);
		}
	}
}
