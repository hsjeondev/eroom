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
	
//	@ManyToOne
//	@JoinColumn(name = "approval_line_no")
//	private ApprovalLine approvalLine; // 결재 라인 번호
	
	@Column(name = "approval_alarm_comment")
	private String approvalAlarmComment; // 결재 알람 내용
	
	@Column(name = "approval_alarm_read_yn")
	private String approvalAlarmReadYn; // 결재 알람 읽음 여부
	
	@Column(name = "approval_alarm_reg_date", insertable = false, updatable = false)
	private LocalDateTime approvalAlarmRegDate; // 결재 알람 등록일
	
    @ManyToOne
    @JoinColumn(name = "employee_no")
    private Employee receiver; // 알림 받는 사람 (기안자든 결재자든 상관없이)
    
    @ManyToOne
    @JoinColumn(name = "approval_no") 
    private Approval approval; // 결재 문서 기준으로 FK 연결
}
