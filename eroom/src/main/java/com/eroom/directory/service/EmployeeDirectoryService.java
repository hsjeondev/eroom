package com.eroom.directory.service;

import org.springframework.stereotype.Service;

import com.eroom.directory.repository.EmployeeDirectoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeDirectoryService {

	private final EmployeeDirectoryRepository employeeDirectoryRepository;
	
}
