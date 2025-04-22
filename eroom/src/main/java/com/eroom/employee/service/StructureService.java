package com.eroom.employee.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.employee.entity.Structure;
import com.eroom.employee.repository.StructureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StructureService {
	
	private final StructureRepository structureRepository;
	
	// 부모 코드를 매개변수로 Structure 객체를 조회
	public Structure selectStructureCodeNameByParentCodeEqualsSeparatorCode(String parentCode) {
		return structureRepository.findBySeparatorCode(parentCode);
	}
	// 모든 부서 조회
	public List<Structure> selectDepartmentAll() {
		return structureRepository.findByParentCodeIsNull();
	}
	// 부서코드를 통한 부서 하위의 모든 팀 조회
	public List<Structure> selectTeamAll(String parentCode) {
		return structureRepository.findByParentCode(parentCode);
	}
}
