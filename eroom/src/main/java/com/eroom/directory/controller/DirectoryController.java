package com.eroom.directory.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eroom.directory.dto.DirectoryDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.service.EmployeeDirectoryService;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.StructureService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DirectoryController {
	
	private final EmployeeDirectoryService employeeDirectoryService;
	private final StructureService structureService;
	

	@GetMapping("/directory/employee")
	public String selectDirectoryEmployeeList(Model model) {
		List<DirectoryDto> employeeList = new ArrayList<DirectoryDto>();
		List<Directory> temp = employeeDirectoryService.selectDirectoryEmployeeAllBySeparatorCode();
		
		// 직원 리스트를 가져와서 DTO로 변환
		for(Directory t : temp) {
			DirectoryDto dto = new DirectoryDto().toDto(t);
			if (t.getEmployee().getStructure() != null) {
			    Structure structure = t.getEmployee().getStructure();
			    if (structure.getParentCode() != null) {
			    	// 팀이라는 뜻이니까 code_name을 가져오면 팀명
			        dto.setTeam_name(structure.getCodeName());
			        // 부서 있다는 뜻이니까 부서 parent_code=separator_code 조회해서 code_name을 가져오면 부서명
			        Structure parent = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(structure.getParentCode());
			        dto.setDepartment_name(parent != null ? parent.getCodeName() : "-");
			    } else {
			    	// 부서라는 뜻이니까 code_name을 바로 가져오면 부서명
			        dto.setDepartment_name(structure.getCodeName() != null ? structure.getCodeName() : "-");
			        dto.setTeam_name("-");
			    }
			} else {
			    dto.setDepartment_name("-");
			    dto.setTeam_name("-");
			}
			// 재직중인 사람만 리스트에 추가
			if (t.getEmployee().getEmployeeEmploymentYn().equals("Y")) {
				employeeList.add(dto);
			} 
		}
		
		// 부서 리스트와 팀 리스트를 가져와서 Map에 저장
		List<Structure> departmentList = new ArrayList<Structure>();
		Map<String, List<Structure>> teamMap = new HashMap<String, List<Structure>>();
		departmentList = structureService.selectDepartmentAll();
		for (Structure s : departmentList) {
			// 부서명으로 팀 리스트를 가져와서 Map에 저장
			List<Structure> teamList = new ArrayList<Structure>();
			// separator_code로 팀 리스트를 조회 후 <부서명, 팀리스트> 형태로 Map에 저장
			teamList = structureService.selectTeamAll(s.getSeparatorCode());
			teamMap.put(s.getCodeName(), teamList);
		}
		
		
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("teamMap", teamMap);
		model.addAttribute("employeeList", employeeList);
		
		return "directory/employeeList";
	}
	@GetMapping("/directory/partner")
	public String selectDirectoryPartnerList(Model model) {
//		List<EmployeeDirectoryDto> resultList = new ArrayList<EmployeeDirectoryDto>();
//		List<EmployeeDirectory> temp = employeeDirectoryService.selectDirectoryAll();
//		
//		for(EmployeeDirectory t : temp) {
//			EmployeeDirectoryDto dto = new EmployeeDirectoryDto();
//			resultList.add(dto.toDto(t));
//		}
//		
//		model.addAttribute("resultList", resultList);
		
		return "directory/partnerList";
//		return "directory/sample";
	}
	
}
