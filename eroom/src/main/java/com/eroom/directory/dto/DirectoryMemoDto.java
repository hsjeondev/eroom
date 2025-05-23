package com.eroom.directory.dto;

import java.time.LocalDateTime;

import com.eroom.directory.entity.Directory;
import com.eroom.directory.entity.DirectoryMemo;
import com.eroom.employee.entity.Employee;

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
public class DirectoryMemoDto {
	private Long directory_memo_no;
	private String directory_memo_content;
	private String directory_memo_creator;
	private String directory_memo_editor;
	private String visible_yn;
	private LocalDateTime directory_memo_reg_date;
	private LocalDateTime directory_memo_mod_date;
	private Long directory_no;
	private Long employee_no;
	
	public DirectoryMemo toEntity() {
		return DirectoryMemo.builder()
				.directoryMemoNo(directory_memo_no)
				.directoryMemoContent(directory_memo_content)
				.directoryMemoCreator(directory_memo_creator)
				.directoryMemoEditor(directory_memo_editor)
				.visibleYn(visible_yn)
				.directory(Directory.builder().directoryNo(directory_no).build())
				.employee(Employee.builder().employeeNo(employee_no).build())
				.build();
	}
	public DirectoryMemoDto toDto(DirectoryMemo entity) {
		return DirectoryMemoDto.builder()
				.directory_memo_no(entity.getDirectoryMemoNo())
				.directory_memo_content(entity.getDirectoryMemoContent())
				.directory_memo_creator(entity.getDirectoryMemoCreator())
				.directory_memo_editor(entity.getDirectoryMemoEditor())
				.visible_yn(entity.getVisibleYn())
				.directory_memo_reg_date(entity.getDirectoryMemoRegDate())
				.directory_memo_mod_date(entity.getDirectoryMemoModDate())
				.directory_no(entity.getDirectory().getDirectoryNo())
				.employee_no(entity.getEmployee().getEmployeeNo())
				.build();
	}
	
	
}
