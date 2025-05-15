package com.eroom.directory.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.directory.dto.AddDepartmentAndTeamDto;
import com.eroom.directory.dto.DeleteDepartmentOrTeamDto;
import com.eroom.directory.dto.DirectoryDto;
import com.eroom.directory.dto.GetSortableDto;
import com.eroom.directory.dto.GetSortableTaemDto;
import com.eroom.directory.dto.UpdateSortOrderDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.service.DirectoryBookmarkService;
import com.eroom.directory.service.DirectoryService;
import com.eroom.drive.service.ProfileService;
import com.eroom.employee.dto.EmployeeDto;
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
	private final ProfileService profileService;
	private final DirectoryBookmarkService directoryBookmarkService;
	

	@GetMapping("/directory/employee")
	public String selectDirectoryEmployeeList(@RequestParam(name="deptId",required=false) Long deptId, @RequestParam(name="teamId",required=false) Long teamId,Model model, @AuthenticationPrincipal EmployeeDetails user) {
		Employee employee = user.getEmployee();
//		model.addAttribute("employee", employee);
		model.addAttribute("employeeDetails", user);
//		System.out.println(user.getAuthorities() + "123");
		List<DirectoryDto> employeeList = new ArrayList<DirectoryDto>();
		List<DirectoryDto> employeeListBookmark = new ArrayList<DirectoryDto>();
		List<Directory> temp = directoryService.selectDirectoryEmployeeAllBySeparatorCode();
		
		// 직원 리스트를 가져와서 DTO로 변환
		for(Directory t : temp) {
			DirectoryDto dto = new DirectoryDto().toDto(t);
			// 프로필 이미지 url 조회
			String profileUrl = profileService.getProfileImageUrl(t.getEmployee().getEmployeeNo());
			if(profileUrl != null) {
				dto.setProfileImageUrl(profileUrl);
			}
			
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
				String bookmarkYn = directoryBookmarkService.findBookmarkYnByEmployeeNo(employee.getEmployeeNo(), dto.getEmployee_no());
				if(bookmarkYn != null && bookmarkYn.equals("Y")) {
					dto.setBookmark_yn(bookmarkYn);
					dto.setStar_mark_html("<i class=\"fas fa-star\"></i>");
					employeeListBookmark.add(dto);
				} else {
					employeeList.add(dto);
				}
			} 
		}
		
		
		// 부서 리스트와 팀 리스트를 가져와서 Map에 저장
		List<Structure> departmentList = new ArrayList<Structure>();
		Map<String, List<Structure>> teamMap = new HashMap<String, List<Structure>>();
		departmentList = structureService.selectDepartmentAll();
		for (Structure s : departmentList) {
			// 부서명으로 팀 리스트를 가져와서 Map에 저장
			List<Structure> teamListByParentCode = new ArrayList<Structure>();
			// separator_code로 팀 리스트를 조회 후 <부서명, 팀리스트> 형태로 Map에 저장
			teamListByParentCode = structureService.selectTeamAllByParentCode(s.getSeparatorCode());
			teamMap.put(s.getCodeName(), teamListByParentCode);
		}
		// 팀 리스트 조회
		List<Structure> teamList = structureService.findOnlyTeamsVisibleY();
		model.addAttribute("teamList", teamList);
		
		
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("teamMap", teamMap);
		model.addAttribute("employeeList", employeeList);
		model.addAttribute("employeeListBookmark", employeeListBookmark);
		
		return "directory/employeeList";
	}
	// 트리 선택시 비동기 방식으로 회원 리스트 조회
	@GetMapping("/directory/employeeList")
	public String getEmployeeListFragment(@RequestParam("separatorCode") String separatorCode, Model model, @AuthenticationPrincipal EmployeeDetails user) {
//	    List<DirectoryDto> employeeList = new ArrayList<DirectoryDto>();
//		List<Directory> temp = directoryService.selectDirectoryEmployeeAllBySeparatorCode();
//		
//		// 직원 리스트를 가져와서 DTO로 변환
//		for(Directory t : temp) {
//			DirectoryDto dto = new DirectoryDto().toDto(t);
//			if (t.getEmployee().getStructure() != null) {
//			    Structure structure = t.getEmployee().getStructure();
//			    if (structure.getParentCode() != null) {
//			    	// 팀이라는 뜻이니까 code_name을 가져오면 팀명
//			        dto.setTeam_name(structure.getCodeName());
//			        // 부서 있다는 뜻이니까 부서 parent_code=separator_code 조회해서 code_name을 가져오면 부서명
//			        Structure parent = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(structure.getParentCode());
//			        dto.setDepartment_name(parent != null ? parent.getCodeName() : "-");
//			    } else {
//			    	// 부서라는 뜻이니까 code_name을 바로 가져오면 부서명
//			        dto.setDepartment_name(structure.getCodeName() != null ? structure.getCodeName() : "-");
//			        dto.setTeam_name("-");
//			    }
//			} else {
//			    dto.setDepartment_name("-");
//			    dto.setTeam_name("-");
//			}
//			// 재직중인 사람만 리스트에 추가
//			Structure targetStructure = structureService.getBySeparatorCode(separatorCode);
//			String targetCodeName = targetStructure != null ? targetStructure.getCodeName() : null;
//			if (t.getEmployee().getEmployeeEmploymentYn().equals("Y")
//				    && targetCodeName != null
//				    && (targetCodeName.equals(dto.getTeam_name()) || targetCodeName.equals(dto.getDepartment_name()))) {
//				employeeList.add(dto);
//			} else if(t.getEmployee().getEmployeeEmploymentYn().equals("Y")
//				    && separatorCode.equals("selectAll")) {
//				employeeList.add(dto);
//				
//			}
//		}
//		
//		// 부서 리스트와 팀 리스트를 가져와서 Map에 저장
//		List<Structure> departmentList = new ArrayList<Structure>();
//		Map<String, List<Structure>> teamMap = new HashMap<String, List<Structure>>();
//		departmentList = structureService.selectDepartmentAll();
//		for (Structure s : departmentList) {
//			// 부서명으로 팀 리스트를 가져와서 Map에 저장
//			List<Structure> teamList = new ArrayList<Structure>();
//			// separator_code로 팀 리스트를 조회 후 <부서명, 팀리스트> 형태로 Map에 저장
//			teamList = structureService.selectTeamAll(s.getSeparatorCode());
//			teamMap.put(s.getCodeName(), teamList);
//		}
//		
//		
//		model.addAttribute("departmentList", departmentList);
//		model.addAttribute("teamMap", teamMap);
//		model.addAttribute("employeeList", employeeList);
		
		try {
			getEmployeeListBySeparatorCodeMethod(separatorCode, model, user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    
	    return "directory/employeeListFragment :: employeeListFragment";
	}
	
	// fragment 말고 model로 비동기 반환
	@GetMapping("/directory/searchEmployeeList")
	@ResponseBody
	public Map<String, Object> getEmployeeListBySeparatorCode(@RequestParam("separatorCode") String separatorCode, Model model, @AuthenticationPrincipal EmployeeDetails user){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("res_code", "500");
		map.put("res_msg", "조회 실패");
		try {
			Map<String, Object> result = getEmployeeListBySeparatorCodeMethod(separatorCode, model, user);
			map.put("res_code", "200");
			map.put("res_msg", "조회 성공");
			map.put("result", result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	// 비동기 방식으로 회원 리스트 조회하는 공용 메소드
	public Map<String, Object> getEmployeeListBySeparatorCodeMethod(String separatorCode, Model model, @AuthenticationPrincipal EmployeeDetails user) {
		Employee employee = user.getEmployee();
		
		List<DirectoryDto> employeeList = new ArrayList<DirectoryDto>();
		List<DirectoryDto> employeeListBookmark = new ArrayList<DirectoryDto>();
		List<Directory> temp = directoryService.selectDirectoryEmployeeAllBySeparatorCode();
		List<EmployeeDto> searchRemainEmployee = new ArrayList<EmployeeDto>();
		Map<String, Object> result = new HashMap<String, Object>();
		
		// 직원 리스트를 가져와서 DTO로 변환
		for(Directory t : temp) {
			DirectoryDto dto = new DirectoryDto().toDto3(t);
			// 프로필 이미지 url 조회
			String profileUrl = profileService.getProfileImageUrl(t.getEmployee().getEmployeeNo());
			dto.setProfileImageUrl(profileUrl);
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
				String bookmarkYn = directoryBookmarkService.findBookmarkYnByEmployeeNo(employee.getEmployeeNo(), dto.getEmployee_no());
				if(bookmarkYn != null && bookmarkYn.equals("Y")) {
					dto.setBookmark_yn(bookmarkYn);
					dto.setStar_mark_html("<i class=\"fas fa-star\"></i>");
					employeeListBookmark.add(dto);
				} else {
					employeeList.add(dto);
				}
				searchRemainEmployee.add(new EmployeeDto().toDto(t.getEmployee()));
			} else if(t.getEmployee().getEmployeeEmploymentYn().equals("Y")
					&& separatorCode.equals("selectAll")) {
				String bookmarkYn = directoryBookmarkService.findBookmarkYnByEmployeeNo(employee.getEmployeeNo(), dto.getEmployee_no());
				if(bookmarkYn != null && bookmarkYn.equals("Y")) {
					dto.setBookmark_yn(bookmarkYn);
					dto.setStar_mark_html("<i class=\"fas fa-star\"></i>");
					employeeListBookmark.add(dto);
				} else {
					employeeList.add(dto);
				}
				searchRemainEmployee.add(new EmployeeDto().toDto(t.getEmployee()));
				
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
			teamList = structureService.selectTeamAllByParentCode(s.getSeparatorCode());
			teamMap.put(s.getCodeName(), teamList);
		}
		
		
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("teamMap", teamMap);
		model.addAttribute("employeeList", employeeList);
		model.addAttribute("employeeListBookmark", employeeListBookmark);
		model.addAttribute("searchRemainEmployee", searchRemainEmployee);
		result.put("searchRemainEmployee", searchRemainEmployee);
		return result;
	}

	
	
	
	// 협력업체 조회
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
	
	// 협력업체 추가
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
	// 협력업체 수정
	@PutMapping("/directory/partner/update")
	@ResponseBody
	public Map<String, String> updatePartner(@RequestBody Map<String, String> formData, Authentication authentication){
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetail.getEmployee();
		
		
		
		Map<String, String> map = new HashMap<String, String>();
		
		
		map.put("res_code", "500");
		map.put("res_msg", "협력업체 인원 수정을 실패했습니다.");
		
		int result = directoryService.updatePartner(formData, employee);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "협력업체 인원 수정이 완료되었습니다.");
		}
		
		return map;
	}
	// 협력업체 삭제
	@PutMapping("/directory/partner/delete")
	@ResponseBody
	public Map<String, String> deletePartner(@RequestBody Map<String, String> data, Authentication authentication){
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetail.getEmployee();
		
		
		
		Map<String, String> map = new HashMap<String, String>();
		
		
		map.put("res_code", "500");
		map.put("res_msg", "협력업체 인원 삭제를 실패했습니다.");
		
		int result = directoryService.deletePartner(data, employee);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "협력업체 인원 삭제 완료되었습니다.");
		}
		
		return map;
	}
	
	// 부서코드로 팀 조회
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
	
	// 부서, 팀 추가
	@PostMapping("/admin/directory/addDepartmentOrTeam")
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
		
		map.put("res_code", "500");
		map.put("res_msg", type + " 추가에 실패했습니다.");
		
		
		int result = 0;
		result = structureService.addDepartmentOrTeam(dto, employee);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", type + " 추가가 완료됐습니다.");
		}
		
		return map;
	}
	// 부서, 팀 정렬
	@PutMapping("/admin/directory/updateSortOrder")
	@ResponseBody
	public Map<String, String> updateSortOrderMethod(@RequestBody UpdateSortOrderDto dto, Authentication authentication){
		EmployeeDetails employeeDetails = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();

		Map<String, String> map = new HashMap<String, String>();
		
		map.put("res_code", "500");
		map.put("res_msg", "정렬에 실패했습니다.");
		if(dto == null) {
			map.put("res_code", "500");
			map.put("res_msg", "잘못된 요청입니다.");
			return map;
		}
		
		int result = 0;
		
		result = structureService.updateSortOrderMethod(dto, employee);
		
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "정렬이 완료됐습니다.");
		}
		
		return map;
	}
	// Sortable.js 정렬 불러오기 부서, 팀 정렬
	@GetMapping("/admin/directory/getSortOrder")
	@ResponseBody
	public List<GetSortableDto> getSortOrderMethod(Authentication authentication) {
	    List<Structure> departmentList = structureService.selectDepartmentAll();
	    List<GetSortableDto> responseList = new ArrayList<>();

	    for (int i = 0; i < departmentList.size(); i++) {
	        Structure department = departmentList.get(i);

	        // 팀 정보 조회
	        List<Structure> teamList = structureService.selectTeamAllByParentCode(department.getSeparatorCode());
	        List<GetSortableTaemDto> teamDtoList = new ArrayList<>();

	        for (int j = 0; j < teamList.size(); j++) {
	            Structure team = teamList.get(j);
	            teamDtoList.add(GetSortableTaemDto.builder()
	                    .teamId(team.getSeparatorCode())
	                    .teamName(team.getCodeName())
	                    .order(j + 1) // 순서 정보 추가
	                    .build());
	        }

	        // 부서 + 팀 정보 DTO 생성
	        responseList.add(GetSortableDto.builder()
	            .departmentId(department.getSeparatorCode())
	            .departmentName(department.getCodeName())
	            .order(i + 1) // 부서 순서 추가
	            .teams(teamDtoList)
	            .build());
	    }

	    return responseList;
	}

	
	// 부서, 팀 삭제
//	@PutMapping("/admin/employee/delete")
//	@ResponseBody
//	public Map<String, String> deleteDepartmentTeamMethod(@RequestBody AddDepartmentAndTeamDto dto, @AuthenticationPrincipal EmployeeDetails user){
//		Employee employee = user.getEmployee();
//		
//		Map<String, String> map = new HashMap<String, String>();
//		if(dto == null) {
//			map.put("res_code", "500");
//			map.put("res_msg", "잘못된 요청입니다.");
//			return map;
//		}
//		
//		
//		String type = (dto.getParentCode() == null) ? "부서" : "팀";
//		
//		map.put("res_code", "500");
//		map.put("res_msg", type + " 삭제 실패했습니다.");
//		
//		
//		int result = 0;
//		result = structureService.deleteDepartmentOrTeam(dto, employee);
//		
//		if(result > 0) {
//			map.put("res_code", "200");
//			map.put("res_msg", type + " 삭제가 완료됐습니다.");
//		}
//		
//		
//		return map;
//	}
	
	@PutMapping("/admin/directory/deleteDepartmentOrTeam")
	@ResponseBody
	public Map<String, String> deleteDepartmentTeamMethod(@RequestBody DeleteDepartmentOrTeamDto dto, @AuthenticationPrincipal EmployeeDetails user){
		Employee employee = user.getEmployee();
		
		Map<String, String> map = new HashMap<String, String>();
		if(dto == null) {
			map.put("res_code", "500");
			map.put("res_msg", "잘못된 요청입니다.");
			return map;
		}
		
		
		String type = (dto.getDeleteTeamCode() == null || dto.getDeleteTeamCode().equals("0")) ? "부서" : "팀";
		// System.out.println(dto);
		map.put("res_code", "500");
		map.put("res_msg", type + " 삭제 실패했습니다.");
		
		
		int result = 0;
		result = structureService.deleteDepartmentOrTeam(dto, employee);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", type + " 삭제가 완료됐습니다.");
		}
		
		
		return map;
	}
	


	
	
}
