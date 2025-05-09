package com.eroom.directory.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eroom.directory.dto.DirectoryDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.service.DirectoryService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.EmployeeService;
import com.eroom.employee.service.StructureService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TreeModalController {
	
	private final DirectoryService directoryService;
	private final StructureService structureService;
	private final EmployeeService employeeService;

	
	@GetMapping("/tree")
	public String treeTest(Model model) {
		 Map<String, List<EmployeeDto>> teamEmployeeMap = new HashMap<>();
		 List<Employee> empEntityList = employeeService.findAllEmployee();
		 // 팀 조회
		 
		
		// 부서 리스트와 팀 리스트를 가져와서 Map에 저장
		List<Structure> departmentList = new ArrayList<Structure>();
		Map<String, List<Structure>> teamMap = new HashMap<String, List<Structure>>();
		departmentList = structureService.selectDepartmentAll();
		for (Structure s : departmentList) {
			// 부서명으로 팀 리스트를 가져와서 Map에 저장
			List<Structure> teamList = new ArrayList<Structure>();
			// separator_code로 팀 리스트를 조회 후 <부서명, 팀리스트> 형태로 Map에 저장
			teamList = structureService.selectTeamAllByParentCode(s.getSeparatorCode());
			teamMap.put(s.getCodeName(), teamList);
			for(Structure team : teamList) {
				 List<EmployeeDto> employeeList = new ArrayList<EmployeeDto>();
				for(Employee empEntity : empEntityList) {
					if(empEntity.getStructure() != null 
						    && empEntity.getStructure().getSeparatorCode().equals(team.getSeparatorCode())) {
						employeeList.add(new EmployeeDto().toDto(empEntity));
					}
				}
				teamEmployeeMap.put(team.getSeparatorCode(), employeeList);
			}
		}
		// 부서&&팀 없는 사람
		List<EmployeeDto> noTeamList = new ArrayList<>();
		for (Employee empEntity : empEntityList) {
			if (empEntity.getStructure() == null) {
				EmployeeDto dto = new EmployeeDto().toDto(empEntity);
				noTeamList.add(dto);
			}
		}
		teamEmployeeMap.put("noTeam", noTeamList);
		
		// 부서는 있지만 팀이 없는 사람
		for (Structure dept : departmentList) {
		    List<EmployeeDto> notAssignedList = new ArrayList<>();
		    for (Employee empEntity : empEntityList) {
		        Structure empStruct = empEntity.getStructure();

		        if (empStruct != null) {
		            // ① 구조 자체가 부서에 해당 (즉, 구조 자체가 이 부서와 동일)
		            if (empStruct.getSeparatorCode().equals(dept.getSeparatorCode())) {
		                notAssignedList.add(new EmployeeDto().toDto(empEntity));
		            }

		            // ② 혹은 이 부서의 자식인데 팀 소속이 아님
		            else if (empStruct.getParentCode() != null &&
		                     empStruct.getParentCode().equals(dept.getSeparatorCode())) {
		                List<Structure> teamList = teamMap.get(dept.getCodeName());
		                boolean isTeam = teamList.stream()
		                    .anyMatch(t -> t.getSeparatorCode().equals(empStruct.getSeparatorCode()));
		                if (!isTeam) {
		                    notAssignedList.add(new EmployeeDto().toDto(empEntity));
		                }
		            }
		        }
		    }

		    if (!notAssignedList.isEmpty()) {
		        teamEmployeeMap.put(dept.getSeparatorCode() + "_notAssigned", notAssignedList);
		    }
		}

		
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("teamMap", teamMap);
		model.addAttribute("teamEmployeeMap", teamEmployeeMap);
		
		return "/directory/treeModal";
	}
	
	public void treeControllerMethod(Model model) {

		 Map<String, List<EmployeeDto>> teamEmployeeMap = new HashMap<>();
		 List<Employee> empEntityList = employeeService.findAllEmployee();
		 // 팀 조회
		 
		
		// 부서 리스트와 팀 리스트를 가져와서 Map에 저장
		List<Structure> departmentList = new ArrayList<Structure>();
		Map<String, List<Structure>> teamMap = new HashMap<String, List<Structure>>();
		departmentList = structureService.selectDepartmentAll();
		for (Structure s : departmentList) {
			// 부서명으로 팀 리스트를 가져와서 Map에 저장
			List<Structure> teamList = new ArrayList<Structure>();
			// separator_code로 팀 리스트를 조회 후 <부서명, 팀리스트> 형태로 Map에 저장
			teamList = structureService.selectTeamAllByParentCode(s.getSeparatorCode());
			teamMap.put(s.getCodeName(), teamList);
			for(Structure team : teamList) {
				 List<EmployeeDto> employeeList = new ArrayList<EmployeeDto>();
				for(Employee empEntity : empEntityList) {
					if(empEntity.getStructure() != null 
						    && empEntity.getStructure().getSeparatorCode().equals(team.getSeparatorCode())) {
						employeeList.add(new EmployeeDto().toDto(empEntity));
					}
				}
				teamEmployeeMap.put(team.getSeparatorCode(), employeeList);
			}
		}
		// 부서&&팀 없는 사람
		List<EmployeeDto> noTeamList = new ArrayList<>();
		for (Employee empEntity : empEntityList) {
			if (empEntity.getStructure() == null) {
				EmployeeDto dto = new EmployeeDto().toDto(empEntity);
				noTeamList.add(dto);
			}
		}
		teamEmployeeMap.put("noTeam", noTeamList);
		
		// 부서는 있지만 팀이 없는 사람
		for (Structure dept : departmentList) {
		    List<EmployeeDto> notAssignedList = new ArrayList<>();
		    for (Employee empEntity : empEntityList) {
		        Structure empStruct = empEntity.getStructure();

		        if (empStruct != null) {
		            // ① 구조 자체가 부서에 해당 (즉, 구조 자체가 이 부서와 동일)
		            if (empStruct.getSeparatorCode().equals(dept.getSeparatorCode())) {
		                notAssignedList.add(new EmployeeDto().toDto(empEntity));
		            }

		            // ② 혹은 이 부서의 자식인데 팀 소속이 아님
		            else if (empStruct.getParentCode() != null &&
		                     empStruct.getParentCode().equals(dept.getSeparatorCode())) {
		                List<Structure> teamList = teamMap.get(dept.getCodeName());
		                boolean isTeam = teamList.stream()
		                    .anyMatch(t -> t.getSeparatorCode().equals(empStruct.getSeparatorCode()));
		                if (!isTeam) {
		                    notAssignedList.add(new EmployeeDto().toDto(empEntity));
		                }
		            }
		        }
		    }

		    if (!notAssignedList.isEmpty()) {
		        teamEmployeeMap.put(dept.getSeparatorCode() + "_notAssigned", notAssignedList);
		    }
		}

		
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("teamMap", teamMap);
		model.addAttribute("teamEmployeeMap", teamEmployeeMap);
	}
}
