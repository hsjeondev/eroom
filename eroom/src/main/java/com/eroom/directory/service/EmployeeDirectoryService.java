package com.eroom.directory.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.directory.entity.Directory;
import com.eroom.directory.repository.EmployeeDirectoryRepository;
import com.eroom.employee.entity.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeDirectoryService {

	private final EmployeeDirectoryRepository employeeDirectoryRepository;

	public List<Directory> selectDirectoryAll() {
		return employeeDirectoryRepository.findAll();
	}

//	public void selectDirectoryByDepartmentNo(Long id) {
//		return employeeDirectoryRepository.findById(null)
//		
//	}
	
}
