package com.eroom.directory.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Separator;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.entity.MailReceiver;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name="directory")
public class Directory {
	
	@Id
	@Column(name="directory_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long directoryNo; // 직원 주소록 번호
	
	@Column(name = "directory_email")
	private String directoryEmail; // 이메일
	@Column(name = "directory_phone")
	private String directoryPhone; // 휴대폰 번호
	@Column(name = "directory_name")
	private String directoryName; // 이름
	@Column(name = "directory_company_name")
	private String directoryCompanyName; // 회사명
	@Column(name = "directory_creator")
	private String directoryCreator; // 생성자
	@Column(name = "directory_editor")
	private String directoryEditor; // 수정자
	@Column(name = "visible_yn")
	private String visibleYn; // 사용여부
	@Column(name = "directory_reg_date", insertable = false, updatable = false)
	private LocalDateTime directoryRegDate; // 생성시간
	@Column(name = "directory_mod_date")
	private LocalDateTime directoryModDate; // 수정시간
	@Column(name = "directory_position")
	private String directoryPosition; // 직급
	@Column(name = "directory_department")
	private String directoryDepartment; // 부서명
	@Column(name = "directory_team")
	private String directoryTeam; // 팀명
	
	@OneToOne
	@ToString.Exclude
	@JoinColumn(name = "employee_no")
	private Employee employee; // 사번
	
	@ManyToOne
	@JoinColumn(name = "separator_code")
	private Separator separator;
	
	@OneToMany(mappedBy = "directory")
	private List<DirectoryMemo> directoryMemos;

}
