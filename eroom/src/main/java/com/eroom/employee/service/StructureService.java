package com.eroom.employee.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.eroom.directory.dto.AddDepartmentAndTeamDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Separator;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.repository.SeparatorRepository;
import com.eroom.employee.repository.StructureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StructureService {
	
	private final StructureRepository structureRepository;
	private final SeparatorRepository separatorRepository;
	
	// 부모 코드를 매개변수로 Structure 객체를 조회
	public Structure selectStructureCodeNameByParentCodeEqualsSeparatorCode(String parentCode) {
		return structureRepository.findBySeparatorCode(parentCode);
	}
	// 모든 부서 조회
	public List<Structure> selectDepartmentAll() {
//		return structureRepository.findByParentCodeIsNull(); 
		return structureRepository.findByVisibleYnAndParentCodeIsNullOrderBySortOrderAsc("Y");
	}
	// 부서코드를 통한 부서 하위의 모든 팀 조회
	public List<Structure> selectTeamAll(String parentCode) {
//		return structureRepository.findByParentCode(parentCode);
		return structureRepository.findByParentCodeAndVisibleYnOrderBySortOrderAsc(parentCode, "Y");
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
	// 특정 부서 separatorCode로 하위 팀 separatorCodes를 조회
	public List<Structure> findChildSeparatorCodes(String departmentSeparatorCode) {
	    List<Structure> teams = structureRepository.findByParentCodeAndVisibleYnOrderBySortOrderAsc(departmentSeparatorCode, "Y");
//	    List<String> codes = new ArrayList<>();
//	    for (Structure team : teams) {
//	        if ("Y".equals(team.getVisibleYn())) { // visibleYn이 'Y'인 팀만
//	            codes.add(team.getSeparatorCode());
//	        }
//	    }
	    return teams;
	}
	
	// admin의 부서, 팀 추가
	public int addDepartmentOrTeam(AddDepartmentAndTeamDto dto, Employee employee) {
		int result = 0;
		try {
			Structure structure = null;
			Separator separator = null;
			List<Structure> structureDB = null;
			boolean exists = false;
			String separatorCode = "";
			if(dto.getParentCode() == null) {
				// 부서 추가
				structureDB = structureRepository.findByVisibleYnAndParentCodeIsNullOrderBySortOrderAsc("Y");
				Long sortOrder = null;
				if (structureDB == null || structureDB.isEmpty()) {
				    sortOrder = 1L;
				    separatorCode = "D001";
				} else {
				    sortOrder = structureDB.get(structureDB.size() - 1).getSortOrder() + 1;
				    String tempSepCode = structureDB.get(structureDB.size() - 1).getSeparatorCode();
				    StringBuilder temp = new StringBuilder();
				    for(int i = 0; i < tempSepCode.length(); i++) {
				    	if('0' <= tempSepCode.charAt(i) && tempSepCode.charAt(i) <= '9') {
				    		temp.append(tempSepCode.charAt(i));
				    	}
				    }
				    Long tempSepCodeLong = Long.parseLong(temp.toString()) + 1;
				    String padded = StringUtils.leftPad(String.valueOf(tempSepCodeLong), 3, '0');
				    separatorCode = "D" + padded;
				    
				}
				separator = Separator.builder()
							.separatorCode(separatorCode)
							.separatorName(dto.getCodeName())
							.separatorYn("Y")
							.separatorCreator(employee.getEmployeeId())
							.build();
				structure = Structure.builder()
						.codeName(dto.getCodeName())
						.visibleYn("Y")
						.sortOrder(sortOrder)
						.creator(employee.getEmployeeId())
						.separatorCode(separatorCode)
						.build();
				exists = structureRepository.existsByCodeNameAndVisibleYn(dto.getCodeName(), "Y");
			} else {
				// 팀 추가
				structureDB = structureRepository.findByParentCodeAndVisibleYnOrderBySortOrderAsc(dto.getParentCode(), "Y");
				Long sortOrder = null;
				if (structureDB == null || structureDB.isEmpty()) {
				    sortOrder = 1L;
				} else {
				    sortOrder = structureDB.get(structureDB.size() - 1).getSortOrder() + 1;
				}
				structure = Structure.builder()
							.codeName(dto.getCodeName())
							.parentCode(dto.getParentCode())
							.visibleYn("Y")
							.creator(employee.getEmployeeId())
							.sortOrder(sortOrder)
							.build();
				exists = structureRepository.existsByCodeNameAndParentCodeAndVisibleYn(dto.getCodeName(), dto.getParentCode(), "Y");
			}
			// 이름 중복인 경우 돌려보내기
			if (exists) return -1;
			
			structureRepository.save(structure);
			separatorRepository.save(separator);
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	

}
