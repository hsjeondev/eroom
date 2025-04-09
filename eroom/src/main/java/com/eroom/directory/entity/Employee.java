package com.eroom.directory.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.entity.ChatroomAttendee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name="employee")
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long employeeNo; // 사번

	@Column(name="employee_id")
	private String employeeId;

	@Column(name="employee_pw")
	private String employeePw; // 비밀번호
	@Column(name="employee_name")
	private String employeeName; // 이름
	@Column(name="employee_hire_date")
	private LocalDateTime employeeHireDate; // 고용일
	@Column(name="employee_end_date")
	private LocalDateTime employeeEndDate; // 퇴사일
	@Column(name="employee_end_yn")
	private String employeeEndYn; // 퇴사여부
	@Column(name="employee_position")
	private String employeePosition; // 직급	
	
	@ManyToOne
	@JoinColumn(name="department_no")
	private Department department; // 부서번호
	@ManyToOne
	@JoinColumn(name="team_no")
	private Team team; // 팀번호
	
	// 채팅방 조인
	@OneToMany(mappedBy="creater")
	private List<Chatroom> creater;
	// 채팅방매핑 조인
	@OneToMany(mappedBy="formMember")
	private List<ChatroomAttendee> formMember;
}
