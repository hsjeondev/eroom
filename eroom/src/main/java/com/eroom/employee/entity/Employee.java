
package com.eroom.employee.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.eroom.attendance.entity.Attendance;
import com.eroom.chat.entity.Chatroom;
import com.eroom.chat.entity.ChatroomAttendee;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.entity.DirectoryMemo;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.entity.MailReceiver;
import com.eroom.project.entity.ProjectMember;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
	@Column(name="employee_no")
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
	
	@Column(name="employee_employment_yn")
	private String employeeEmploymentYn; // 퇴사여부
	
	@Column(name="employee_position")
	private String employeePosition; // 직급
	
	@ManyToOne
	@JoinColumn(name="structure_no")
	private Structure structure;
	
//	@ManyToOne
//	@JoinColumn(name="department_no")
//	private Department department; // 부서번호
//	@ManyToOne
//	@JoinColumn(name="team_no")
//	private Team team; // 팀번호
	

	// 채팅방 조인
	@OneToMany(mappedBy="creater")
	private List<Chatroom> creater;
	// 채팅방매핑 조인
	@OneToMany(mappedBy="attendee")
	private List<ChatroomAttendee> attendeeList;

	@ManyToMany
	@JoinTable(
			name = "authority_mapping",
			joinColumns = @JoinColumn(name="employee_no"),
			inverseJoinColumns = @JoinColumn(name="authority_no"))
	private List<Authority> authorities;
    
	@OneToOne(mappedBy = "employee")
	private Directory directory;
	
//	//개인 캘린더 조인
//	@OneToMany(mappedBy="employee")
//	private List<EmployeeCalendar> employeeCalendars;
	
	@OneToMany(mappedBy = "employee")
	private List<DirectoryMemo> directoryMemos;
	
	@OneToMany(mappedBy="employee")
	private List<ProjectMember> projectMembers;

	// 메일 조인
	@OneToMany(mappedBy = "sender")
	private List<Mail> sentMails; // 보낸 메일 목록
	
	@OneToMany(mappedBy = "receiver")
	private List<MailReceiver> receivedMails; // 수신한 메일 목록
	
	
}
