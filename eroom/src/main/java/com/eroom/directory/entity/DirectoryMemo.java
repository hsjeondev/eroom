package com.eroom.directory.entity;

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
@Table(name="directory_memo")
public class DirectoryMemo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long directoryMemoNo;
	
	
	@Column(name = "directory_memo_content")
	private String directoryMemoContent;
	@Column(name = "directory_memo_creator")
	private String directoryMemoCreator;
	@Column(name = "directory_memo_editor")
	private String directoryMemoEditor;
	@Column(name = "visible_yn")
	private String visibleYn;
	
	@Column(name = "directory_memo_reg_date")
	private LocalDateTime directoryMemoRegDate;
	@Column(name = "directory_memo_mod_date")
	private LocalDateTime directoryMemoModDate;
	
	@ManyToOne
	@JoinColumn(name = "directory_no")
	private Directory directory;
	@ManyToOne
	@JoinColumn(name = "employee_no")
	private Employee employee;
	
}
