package com.eroom.directory.entity;

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
@Table(name="partner_directory")
public class PartnerDirectory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long partnerDirectoryNo; // 협력업체 주소록 번호
	
	@Column(name = "partner_directory_name")
	private String partnerDirectoryName; // 이름
	@Column(name = "partner_directory_email")
	private String partnerDirectoryEmail; // 이메일
	@Column(name = "partner_directory_phone")
	private String partnerDirectoryPhone; // 휴대폰 번호
	@Column(name = "partner_directory_company_name")
	private String partnerDirectoryCompanyName; // 협력업체 회사명
}
