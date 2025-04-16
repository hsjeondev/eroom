package com.eroom.employee.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.dto.StructureDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.employee.repository.StructureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
	private final EmployeeRepository employeeRepository;
	private final StructureRepository structureRepository;
	
	// 전체 직원 조회
	public List<Employee> findAllEmployee() {
		return employeeRepository.findAll();
	}
	
	// 중복 제거된 부서 이름 조회 후 DTO 목록 반환
	public List<SeparatorDto> findDistinctStructureNames() {
	    List<Structure> structures = employeeRepository.findDistinctStructures();
	    return structures.stream()
	        .map(entity -> SeparatorDto.builder()
	                .separator_code(entity.getSeparatorCode())
	                .separator_name(entity.getCodeName())
	                .build())
	        .collect(Collectors.toList());
	}
	
	public List<EmployeeDto> findEmployeesByStructureName(String separatorCode) {
	    // 부서명으로 직원을 조회
		System.out.println("현재 찾고 있는 부서 코드: " + separatorCode);
		List<Employee> employes = employeeRepository.findByStructure_SeparatorCode(separatorCode);
		List<EmployeeDto> employeeDtos = new ArrayList<>();
		for(Employee emp : employes) {
			EmployeeDto employeeDto = new EmployeeDto(emp.getEmployeeNo(), emp.getEmployeeName());
			employeeDtos.add(employeeDto);
		}
		return employeeDtos;
	}

	public List<EmployeeDto> findEmployeesByParentCode(String parentCode) {
	    // 부모 부서코드로 직원을 조회
		List<Employee> employes = employeeRepository.findByStructureParentCode(parentCode);
		List<EmployeeDto> employeeDtos = new ArrayList<>();
		for(Employee emp : employes) {
			EmployeeDto employeeDto = new EmployeeDto(emp.getEmployeeNo(), emp.getEmployeeName());
			employeeDtos.add(employeeDto);
		}
		return employeeDtos;
	}
	
	
	// 부서 하위의 모든 팀 조회
	public List<StructureDto> findTeams() {
		return structureRepository.findBySeparatorCodeStartingWith("T")
                .stream()
                .map(entity -> StructureDto.toDto(entity))
                .collect(Collectors.toList());
	}

	public List<Employee> findEmployeesByTeamId(String teamId) {
		return employeeRepository.findEmployeesByTeamId(teamId);
	}
}
