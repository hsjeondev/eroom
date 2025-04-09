package com.eroom.directory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name="directory_memo")
public class DirectoryMemo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long directoryMemoNo;
	@Column(name = "directory_memo_target")
	private String directoryMemoTarget;
	@Column(name = "directory_memo_directory_no")
	private Long directoryMemoDirectoryNo;
	@Column(name = "directory_memo_content")
	private String directoryMemoContent;
	
	
	@OneToOne
	@JoinColumn(name = "employee_no")
	private Employee employee;
}
