package com.eroom.approval.dto;

import java.time.LocalDateTime;

import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.entity.ApprovalSignature;

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
	private Long approval_line_no; // 결재 라인 번호
	private String approval_signature_name; // 결재 서명 이름
	private String approval_signature_path; // 결재 서명 경로
	private LocalDateTime approval_signature_mod_date; // 결재 서명 수정일

	public ApprovalSignatureDto toDto(ApprovalSignature entity) {
		return ApprovalSignatureDto.builder()
				.approval_signature_no(entity.getApprovalSignatureNo())
				.approval_signature_name(entity.getApprovalSignatureName())
				.approval_signature_path(entity.getApprovalSignaturePath())
				.approval_signature_mod_date(entity.getApprovalSignatureModDate())
				.build();
	}
	
	public ApprovalSignature toEntity() {
		return ApprovalSignature.builder()
				.approvalSignatureNo(approval_signature_no)
				.approvalSignatureName(approval_signature_name)
				.approvalSignaturePath(approval_signature_path)
				.approvalSignatureModDate(approval_signature_mod_date)
				.build(); 
	}
}
