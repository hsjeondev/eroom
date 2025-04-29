package com.eroom.approval.dto;

import java.time.LocalDateTime;
import java.util.Base64;

import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.entity.ApprovalSignature;
import com.eroom.employee.entity.Employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ApprovalSignatureDto {
	
	private Long approval_signature_no; // 결재 서명 번호
	private String approval_signature_name; // 결재 서명 이름
//	private byte[] approval_signature_blob; // 결재 서명 이미지 바이너리저장
	private String signature_data_url;      // 클라이언트 전송용 Base64 URL
	private Long employee_no;

	public ApprovalSignatureDto toDto(ApprovalSignature entity) {
		// 이미지 바이트 배열을 Base64 문자열로 인코딩하고 data URL 형태로 조합
		String dataUrl = entity.getApprovalSignatureBlob() != null ? "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getApprovalSignatureBlob()) : null;
		return ApprovalSignatureDto.builder()
				.employee_no(entity.getEmployee().getEmployeeNo())
				.approval_signature_no(entity.getApprovalSignatureNo())
				.approval_signature_name(entity.getApprovalSignatureName())
				.signature_data_url(dataUrl)
				.build();
	}
	
	public ApprovalSignature toEntity() {
		// base64 url을 이미지 바이너리 저장을 위해 이미지 바이트 배열로 디코딩
	    byte[] blob = null;
	    if (signature_data_url != null && signature_data_url.contains(",")) {
	      blob = Base64.getDecoder().decode(signature_data_url.split(",", 2)[1]);
	    }
		return ApprovalSignature.builder()
				.employee(Employee.builder().employeeNo(employee_no).build())
				.approvalSignatureNo(approval_signature_no)
				.approvalSignatureName(approval_signature_name)
				.approvalSignatureBlob(blob)
				.build(); 
	}
}
