package com.eroom.directory.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.eroom.directory.entity.Directory;
import com.eroom.directory.repository.EmployeeDirectoryRepository;
import com.eroom.employee.entity.Separator;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.repository.StructureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeDirectoryService {

	private final EmployeeDirectoryRepository employeeDirectoryRepository;
	private final StructureRepository structureRepository;

	public List<Directory> selectDirectoryEmployeeAllBySeparatorCode() {
		List<Directory> list = employeeDirectoryRepository.findBySeparator_SeparatorCode("A001");
		return list;
	}
	public List<Directory> selectDirectoryPartnerAllBySeparatorCode() {
		List<Directory> list = employeeDirectoryRepository.findBySeparator_SeparatorCode("A001");
		return list;
	}

//	public Structure selectStructureCodeNameByParentCodeEqualsSeparatorCode(String parentCode) {
//		return structureRepository.findBySeparatorCode(parentCode);
//	}
//	public List<Structure> selectDepartmentAll() {
//		return structureRepository.findByParentCodeIsNull();
//	}
//	public List<Structure> selectTeamAll(String parentCode) {
//		return structureRepository.findByParentCode(parentCode);
//	}
	
	public Directory selectDirectoryByEmployeeNo(Long employeeNo) {
		return employeeDirectoryRepository.findByEmployee_EmployeeNo(employeeNo);
	}

	
}
