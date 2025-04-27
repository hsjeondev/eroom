package com.eroom.employee.service;

import java.util.ArrayList;
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
	
	// 부서 미선택 시, 전체 팀 조회 -> parentCode != null 이고 visibleYn = 'Y'인 팀들 조회
	public List<Structure> selectAllTeams(){
		List<Structure> all = structureRepository.findAll();
		List<Structure> teams = new ArrayList<>();
		for(Structure s : all) {
			// parentCode 가 있으면 팀
			if(s.getParentCode() != null && "Y".equals(s.getVisibleYn())) {
				teams.add(s);
			}
		}
		return teams;
	}
	
	// separatorCode 를 통한 부서 조회 / parentCode -> 부서코드
	public Structure getBySeparatorCode(String separatorCode) {
		return structureRepository.findBySeparatorCode(separatorCode);
	}
	
	// structure_no 로 Structure 조회
	public Structure getStructureById(Long structureNo) {
		return structureRepository.findById(structureNo).orElse(null);
	}
}
