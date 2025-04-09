package com.eroom.directory.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.directory.entity.EmployeeDirectory;
import com.eroom.directory.repository.EmployeeDirectoryRepository;
import com.eroom.employee.entity.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeDirectoryService {

	private final EmployeeDirectoryRepository employeeDirectoryRepository;

	public List<EmployeeDirectory> selectDirectoryAll() {
		return employeeDirectoryRepository.findAll();
	}
	
}
