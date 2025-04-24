package com.eroom.approval.dto;


import com.eroom.approval.entity.ApprovalFormat;

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
public class ApprovalFormatDto {
	private Long approval_format_no;
	private String approval_format_content;
	private String approval_format_title;
	
	public ApprovalFormat toEntity() {
		return ApprovalFormat.builder()
				.approvalFormatNo(approval_format_no)
				.approvalFormatContent(approval_format_content)
				.approvalFormatTitle(approval_format_title)
				.build();
	}
	
	public ApprovalFormatDto toDto(ApprovalFormat entity) {
		return ApprovalFormatDto.builder()
				.approval_format_no(entity.getApprovalFormatNo())
				.approval_format_content(entity.getApprovalFormatContent())
				.approval_format_title(entity.getApprovalFormatTitle())
				.build();
	}
}
