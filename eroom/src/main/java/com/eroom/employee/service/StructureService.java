package com.eroom.employee.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.directory.dto.AddDepartmentAndTeamDto;
import com.eroom.directory.dto.DeleteDepartmentOrTeamDto;
import com.eroom.directory.dto.DepartmentSortDto;
import com.eroom.directory.dto.TeamSortDto;
import com.eroom.directory.dto.UpdateSortOrderDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Separator;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.employee.repository.SeparatorRepository;
import com.eroom.employee.repository.StructureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StructureService {
	
	private final StructureRepository structureRepository;
	private final SeparatorRepository separatorRepository;
	private final EmployeeRepository employeeRepository;
	
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
	public List<Structure> selectTeamAllByParentCode(String parentCode) {
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
	@Transactional(rollbackFor = Exception.class)
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
				structureDB = structureRepository.findByParentCodeIsNullOrderBySortOrderAsc();
				List<Structure> deptStructureDB = structureRepository.findOnlyDepts();
				Long sortOrder = null;
				if (structureDB == null || structureDB.isEmpty()) {
				    sortOrder = 1L;
				} else {
				    sortOrder = structureDB.get(structureDB.size() - 1).getSortOrder() + 1;
				    
				}
				if(deptStructureDB == null || deptStructureDB.isEmpty()) {
					separatorCode = "D001";
				} else {
					StringBuilder temp = new StringBuilder();
					List<Long> deptPad = new ArrayList<Long>();
					for(int i = 0; i < deptStructureDB.size(); i++) {
						for(int j = 0; j < deptStructureDB.get(i).getSeparatorCode().length(); j ++) {
							if('0' <=  deptStructureDB.get(i).getSeparatorCode().charAt(j) &&  deptStructureDB.get(i).getSeparatorCode().charAt(j) <= '9') {
								temp.append(deptStructureDB.get(i).getSeparatorCode().charAt(j));
							}
						}
						deptPad.add(Long.parseLong(temp.toString()));
						temp.setLength(0);
					}
					Collections.sort(deptPad, Comparator.reverseOrder());
					Long deptSepCodeLong = deptPad.get(0) + 1;
					String padded = StringUtils.leftPad(String.valueOf(deptSepCodeLong), 3, '0');
					
					separatorCode = "D" + padded;
				}
				
				
				separator = Separator.builder()
							.separatorCode(separatorCode)
							.separatorName(dto.getCodeName())
							.visibleYn("Y")
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
				structureDB = structureRepository.findByParentCodeOrderBySortOrderAsc(dto.getParentCode());
				List<Structure> teamStructureDB = structureRepository.findOnlyTeams();
				Long sortOrder = null;
				if (structureDB == null || structureDB.isEmpty()) {
				    sortOrder = 1L;
				} else {
				    sortOrder = teamStructureDB.get(teamStructureDB.size() - 1).getSortOrder() + 1;
				}
				
				if(teamStructureDB == null || teamStructureDB.isEmpty()) {
					separatorCode = "T001";
				} else {
					StringBuilder temp = new StringBuilder();
					List<Long> teamPad = new ArrayList<Long>();
					for(int i = 0; i < teamStructureDB.size(); i++) {
						for(int j = 0; j < teamStructureDB.get(i).getSeparatorCode().length(); j ++) {
							if('0' <=  teamStructureDB.get(i).getSeparatorCode().charAt(j) &&  teamStructureDB.get(i).getSeparatorCode().charAt(j) <= '9') {
								temp.append(teamStructureDB.get(i).getSeparatorCode().charAt(j));
							}
						}
						teamPad.add(Long.parseLong(temp.toString()));
						temp.setLength(0);
					}
					Collections.sort(teamPad, Comparator.reverseOrder());
					Long tempSepCodeLong = teamPad.get(0) + 1;
					String padded = StringUtils.leftPad(String.valueOf(tempSepCodeLong), 3, '0');
					
					separatorCode = "T" + padded;
					
				}
				separator = Separator.builder()
						.separatorCode(separatorCode)
						.separatorName(dto.getCodeName())
						.separatorParentCode(dto.getParentCode())
						.visibleYn("Y")
						.separatorCreator(employee.getEmployeeId())
						.build();
				structure = Structure.builder()
							.separatorCode(separatorCode)
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
	
	// sortable js를 사용한 순서 변경
	@Transactional(rollbackFor = Exception.class)
	public int updateSortOrderMethod(UpdateSortOrderDto dto, Employee employee) {
		int result = 0;
		
		try {
			List<DepartmentSortDto> sortData = dto.getSortData();
			for(int i = 0; i < sortData.size(); i++) {
				// 부서 데이터 가져오기
				DepartmentSortDto deptSortDto = sortData.get(i);
				Structure deptEntity = structureRepository.findBySeparatorCode(deptSortDto.getDepartmentSeparatorCode());
				// 부서 정보 업데이트
				deptEntity.setSortOrder(deptSortDto.getDepartmentSortOrder());
				deptEntity.setEditor(employee.getEmployeeId());
				structureRepository.save(deptEntity);
				List<TeamSortDto> teamDto = deptSortDto.getTeams();
				for(int j = 0; j < teamDto.size(); j++) {
					// 팀 데이터 가져오기
					TeamSortDto teamSortDto = teamDto.get(j);
					Structure teamEntity = structureRepository.findBySeparatorCode(teamSortDto.getTeamSeparatorCode());
					// 팀 정보 업데이트
					teamEntity.setSortOrder(teamSortDto.getTeamSortOrder());
					teamEntity.setParentCode(teamSortDto.getNewDepartmentSeparatorCode());
					teamEntity.setEditor(employee.getEmployeeId());
					structureRepository.save(teamEntity);
				}
			}
			
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// 부서, 팀 삭제 및 사원 팀 옮기기
	@Transactional(rollbackFor = Exception.class)
	public int deleteDepartmentOrTeam(DeleteDepartmentOrTeamDto dto, Employee employee) {
		int result = 0;
		try {
			if(dto == null) {
				return 0;
			}
			// 팀원들 먼저 이동
			if(!dto.getEmployeeNoList().isEmpty() && dto.getEmployeeNoList().size() != 0) {
				List<Long> employeeNoList = dto.getEmployeeNoList();
				Structure structureRemainTeam = structureRepository.findBySeparatorCode(dto.getRemainTeamCode());
				for(Long empNo : employeeNoList) {
					Employee empEntity = employeeRepository.findById(empNo).orElse(null);
					if(empEntity != null && structureRemainTeam != null) {
						empEntity.setStructure(structureRemainTeam);
						employeeRepository.save(empEntity);
					} else {
						return 0;
					}
				}
			}
			
			Structure structureEntity = null;
			Separator separatorEntity = null;
			if(dto.getDeleteTeamCode() == null || dto.getDeleteTeamCode().equals("0")) {
				// 부서인 경우
				// 하위 팀 먼저 삭제 로직 필요
				List<Structure> structureTeamInDeptList = structureRepository.findByParentCodeAndVisibleYnOrderBySortOrderAsc(dto.getDeleteDeptCode(), "Y");
				for(Structure s : structureTeamInDeptList) {
					s.setVisibleYn("N");
					structureRepository.save(s);
				}
				List<Separator> separatorTeamInDeptList = separatorRepository.findBySeparatorParentCodeAndVisibleYn(dto.getDeleteDeptCode(), "Y");
				for(Separator s : separatorTeamInDeptList) {
					s.setVisibleYn("N");
					separatorRepository.save(s);
				}
				
				// 부서 삭제
				structureEntity = structureRepository.findBySeparatorCode(dto.getDeleteDeptCode());
				separatorEntity = separatorRepository.findById(dto.getDeleteDeptCode()).orElse(null);
			} else {
				// 팀인 경우
				structureEntity = structureRepository.findBySeparatorCode(dto.getDeleteTeamCode());
				separatorEntity = separatorRepository.findById(dto.getDeleteTeamCode()).orElse(null);
			}
			if(separatorEntity != null && structureEntity != null) {
				structureEntity.setVisibleYn("N");
				separatorEntity.setVisibleYn("N");
				structureRepository.save(structureEntity);
				separatorRepository.save(separatorEntity);
			} else {
				return 0;
			}
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	// 모든 팀 조회
	public List<Structure> findOnlyTeamsVisibleY() {
		return structureRepository.findOnlyTeamsVisibleY();
	}
	

}
