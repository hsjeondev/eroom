package com.eroom.approval.entity;

import java.time.LocalDateTime;

import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="Approval_alarm")
public class ApprovalAlarm {
	@Id
	@Column(name = "approval_alarm_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long approvalAlarmNo; // 결재 알람 번호
	
	@ManyToOne
	@JoinColumn(name = "approval_line_no")
	private ApprovalLine approvalLine; // 결재 라인 번호
	
	@Column(name = "approval_alarm_comment")
	private String approvalAlarmComment; // 결재 알람 내용
	
	@Column(name = "approval_alarm_read_yn")
	private String approvalAlarmReadYn; // 결재 알람 읽음 여부
	
	@Column(name = "approval_alarm_reg_date")
	private LocalDateTime approvalAlarmRegDate; // 결재 알람 등록일
}
