package com.eroom.directory.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.directory.dto.DirectoryMemoDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.entity.DirectoryMemo;
import com.eroom.directory.repository.DirectoryMemoRepository;
import com.eroom.directory.repository.DirectoryRepository;
import com.eroom.employee.entity.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DirectoryMemoService {
	
	private final DirectoryMemoRepository directoryMemoRepository;
	private final DirectoryRepository directoryRepository;

	public List<DirectoryMemo> selectTargetMemo(Long employeeNo, Long id) {
		List<DirectoryMemo> directoryMemo = directoryMemoRepository.findByEmployee_employeeNoAndDirectory_Employee_employeeNoAndVisibleYn(employeeNo, id, "Y");
		if(directoryMemo != null) {
			return directoryMemo;
		} else {
			return null;
		}
	}

	public int createMemo(DirectoryMemoDto dto, Employee employee) {
		int result = 0;
		try {
			Directory directoryEntity = directoryRepository.findById(dto.getDirectory_no()).orElse(null);
			List<DirectoryMemo> directoryMemo = directoryMemoRepository.findByEmployee_employeeNoAndDirectory_Employee_employeeNoAndVisibleYn(employee.getEmployeeNo(), directoryEntity.getEmployee().getEmployeeNo(), "Y");
			DirectoryMemo directoryMemoEntity = new DirectoryMemo();
			// 메모가 비어있는 경우
			if(dto.getDirectory_memo_content().equals("") || dto.getDirectory_memo_content().length() == 0) {
				// 기존 메모가 있다면 삭제 visible -> N
				if(!directoryMemo.isEmpty()) {
					directoryMemoEntity = directoryMemo.get(directoryMemo.size() - 1);
					directoryMemoEntity.setVisibleYn("N");
				} else {
					return 1;
				}
			} else {
				// 새로운 메모 내용이 있는 경우
				// 원래 그 사람의 메모가 있는 경우
				if(!directoryMemo.isEmpty()) {
					directoryMemoEntity = directoryMemo.get(directoryMemo.size() - 1);
					directoryMemoEntity.setDirectoryMemoContent(dto.getDirectory_memo_content());
				} else {
					// 원래 그 사람의 메모가 없는 경우
					dto.setVisible_yn("Y");
					dto.setEmployee_no(employee.getEmployeeNo());
					directoryMemoEntity = dto.toEntity();
					
				}
			}
			directoryMemoRepository.save(directoryMemoEntity);
			
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
}
