package com.eroom.directory.service;

import org.springframework.stereotype.Service;

import com.eroom.directory.entity.DirectoryMemo;
import com.eroom.directory.repository.DirectoryMemoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DirectoryMemoService {
	
	private final DirectoryMemoRepository directoryMemoRepository;

	public DirectoryMemo selectTargetMemo(Long employeeNo, Long id) {
		DirectoryMemo directoryMemo = directoryMemoRepository.findByEmployee_employeeNoAndDirectory_Employee_employeeNo(employeeNo, id);
		if(directoryMemo != null) {
			return directoryMemo;
		} else {
			return null;
		}
	}
	
	
	
}
