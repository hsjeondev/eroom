package com.eroom.directory.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.directory.dto.AddDepartmentAndTeamDto;
import com.eroom.directory.dto.DirectoryDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.service.DirectoryService;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.EmployeeService;
import com.eroom.employee.service.StructureService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DirectoryController {
	
	private final DirectoryService directoryService;
	private final StructureService structureService;
	private final EmployeeService employeeService;
	

	@GetMapping("/directory/employee")
	public String selectDirectoryEmployeeList(@RequestParam(name="deptId",required=false) Long deptId, @RequestParam(name="teamId",required=false) Long teamId,Model model, Authentication authentication) {
//		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
//		Employee employee = employeeDetail.getEmployee();
//		model.addAttribute("employee", employee);
		
		List<DirectoryDto> employeeList = new ArrayList<DirectoryDto>();
		List<Directory> temp = directoryService.selectDirectoryEmployeeAllBySeparatorCode();
		
		// 직원 리스트를 가져와서 DTO로 변환
		for(Directory t : temp) {
			DirectoryDto dto = new DirectoryDto().toDto(t);
			if (t.getEmployee().getStructure() != null) {
			    Structure structure = t.getEmployee().getStructure();
			    if (structure.getParentCode() != null) {
			    	// 팀이라는 뜻이니까 code_name을 가져오면 팀명
			        dto.setTeam_name(structure.getCodeName());
			        // 개발 초기에 컬럼과 매칭되는 Directory_team 필드를 사용하면 안될 것으로 생각해서 위처럼 따로 team_name이라는 필드를 사용했는데.. 잘못생각했던 거 같음.
			        dto.setDirectory_team(structure.getCodeName());
			        // 부서 있다는 뜻이니까 부서 parent_code=separator_code 조회해서 code_name을 가져오면 부서명
			        Structure parent = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(structure.getParentCode());
			        dto.setDepartment_name(parent != null ? parent.getCodeName() : "-");
			        dto.setDirectory_department(parent != null ? parent.getCodeName() : "-");
			    } else {
			    	// 부서라는 뜻이니까 code_name을 바로 가져오면 부서명
			        dto.setDepartment_name(structure.getCodeName() != null ? structure.getCodeName() : "-");
			        dto.setDirectory_department(structure.getCodeName() != null ? structure.getCodeName() : "-");
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
	// 트리 선택시 비동기 방식으로 회원 리스트 조회
	@GetMapping("/directory/employeeList")
	public String getEmployeeListFragment(@RequestParam("separatorCode") String separatorCode, Model model) {
//	    List<Employee> employees = employeeService.findEmployeesByStructureCode(code);
//	    model.addAttribute("employeeList", employees);
//	    System.out.println(separatorCode + " : 코드네임(부서명,팀명)");
	    List<DirectoryDto> employeeList = new ArrayList<DirectoryDto>();
		List<Directory> temp = directoryService.selectDirectoryEmployeeAllBySeparatorCode();
		
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
			Structure targetStructure = structureService.getBySeparatorCode(separatorCode);
			String targetCodeName = targetStructure != null ? targetStructure.getCodeName() : null;
			if (t.getEmployee().getEmployeeEmploymentYn().equals("Y")
				    && targetCodeName != null
				    && (targetCodeName.equals(dto.getTeam_name()) || targetCodeName.equals(dto.getDepartment_name()))) {
				employeeList.add(dto);
			} else if(t.getEmployee().getEmployeeEmploymentYn().equals("Y")
				    && separatorCode.equals("selectAll")) {
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
	    
	    
	    return "directory/employeeListFragment :: employeeListFragment";
	}


	
	
	
	
	@GetMapping("/directory/partner")
	public String selectDirectoryPartnerList(Model model) {
		List<DirectoryDto> resultList = new ArrayList<DirectoryDto>();
		List<Directory> temp = directoryService.selectDirectoryPartner();
		
		for(Directory t : temp) {
			DirectoryDto dto = new DirectoryDto();
			resultList.add(dto.toDto2(t));
//			System.out.println(t.getDirectoryNo() + ": 디렉토리넘버!");
		}
		model.addAttribute("resultList", resultList);
		
		return "directory/partnerList";
	}
	
	@PostMapping("/directory/partner/create")
	@ResponseBody
	public Map<String, String> createPartner(@RequestBody Map<String, String> formData, Authentication authentication){
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetail.getEmployee();
				
		
		
		Map<String, String> map = new HashMap<String, String>();

	    
	    map.put("res_code", "500");
	    map.put("res_msg", "협력업체 인원 추가를 실패했습니다.");
	    
	    int result = directoryService.createPartner(formData, employee);
		
	    if(result > 0) {
	    	map.put("res_code", "200");
	    	map.put("res_msg", "협력업체 인원 추가가 완료되었습니다.");
	    }
		
		return map;
	}
	
	// 부서이름으로 팀 조회 // 지우자
	@GetMapping("/directory/teams")
	@ResponseBody
	public List<Map<String, Object>> getTeamsByDept(@RequestParam("separatorCode") String separatorCode) {
//		System.out.println(separatorCode);
	    List<Structure> teamList = structureService.findChildSeparatorCodes(separatorCode);
	    if(separatorCode.equals("selectAll")) {
	    	teamList = structureService.selectDepartmentAll();
	    }
	    List<Map<String, Object>> result = new ArrayList<>();
	    for (Structure team : teamList) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("codeName", team.getCodeName());
	        map.put("separatorCode", team.getSeparatorCode());
	        result.add(map);
	    }
	    return result;
	}
	
//	@PostMapping("/admin/addDepartment")
//	@ResponseBody
//	public Map<String, String> addDepartmentMethod(@RequestBody())
	@PostMapping("/admin/addDepartmentOrTeam")
	@ResponseBody
	public Map<String, String> addDepartmentTeamMethod(@RequestBody AddDepartmentAndTeamDto dto, Authentication authentication){
		EmployeeDetails employeeDetails = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		
		Map<String, String> map = new HashMap<String, String>();
		if(dto == null) {
			map.put("res_code", "500");
			map.put("res_msg", "잘못된 요청입니다.");
			return map;
		}
		
		
		String type = (dto.getParentCode() == null) ? "부서" : "팀";
		int result = 0;
		result = structureService.addDepartmentOrTeam(dto, employee);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", type + " 추가가 완료됐습니다.");
		}
		
		return map;
	}
	

	
	
}
