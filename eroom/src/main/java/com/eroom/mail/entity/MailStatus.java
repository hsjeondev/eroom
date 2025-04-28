package com.eroom.mail.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="mail_status")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="mail_status_no")
	private Long mailStatusNo;
	
	@Column(name="mail_status_deleted_yn")
	private String mailStatusDeletedYn;
	
	@Column(name="mail_status_important_yn")
	private String mailStatusImportantYn;
	
	@Column(name="mail_status_deleted_time")
	private LocalDateTime mailStatusDeletedTime;
	
	@Column(name="maill_status_visible_yn")
	private String maillStatusVisibleYn;
	
	@OneToOne(mappedBy = "mailStatus")
    private Mail mail; // 연관된 Mail 엔티티
}
