package com.eroom.approval.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@ToString
@Builder

@Entity
@Table(name="approval_format")
public class ApprovalFormat {
	@Id
	@Column(name="approval_format_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long approvalFormatNo;
	
	@Column(name="approval_format_content")
	private String approvalFormatContent;
	
	@Column(name="approval_format_title")
	private String approvalFormatTitle;
}
