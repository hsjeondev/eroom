package com.eroom.project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;
import com.eroom.project.dto.GithubPullRequestDto;
import com.eroom.project.dto.ProjectDto;
import com.eroom.project.dto.ProjectMemberDto;
import com.eroom.project.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
	
	private final EmployeeService employeeService;
	private final ProjectService projectService;

	@GetMapping("/all")
	public String allProjectView(Model model) {
		
		List<ProjectDto> projectList = projectService.findAllProject();
		Long projectCount = projectService.getProjectCount();
		
		model.addAttribute("projectList", projectList);
		model.addAttribute("projectCount", projectCount);
		
		return "project/allProject";
	}
	
	@GetMapping("/doing")
	public String doingProjectView() {
		return "project/doingProject";
	}
	
	@GetMapping("/done")
	public String doneProjectView() {
		return "project/doneProject";
	}
	
	@GetMapping("/hold")
	public String holdProjectView() {
		return "project/holdProject";
	}
	
	@GetMapping("/yet")
	public String yetProjectView() {
		return "project/yetProject";
	}
	
	// 이 컨트롤러 나중에 개발탭으로 사용
	@GetMapping("/detail/{project_no}")
	public String detailProjectView(@PathVariable("project_no") Long project_no, Model model) {
		
		ProjectDto project = projectService.findByProjectNo(project_no);
		model.addAttribute("project", project);
		
	    List<GithubPullRequestDto> pullRequests = projectService.fetchPullRequests(project_no);
	    model.addAttribute("pullRequests", pullRequests);
		
		model.addAttribute("project", project);
		return "project/projectDetail";
	}
	
	@GetMapping("/detail/{project_no}/main")
	public String detailMainProjectView(@PathVariable("project_no") Long project_no, Model model) {
		
		ProjectDto project = projectService.findByProjectNo(project_no);
		model.addAttribute("project", project);
		
		return "project/projectDetailMain";
	}
	
	@GetMapping("/detail/{project_no}/developmentTab")
	public String detailDevelopmentTabProjectView(@PathVariable("project_no") Long project_no, Model model) {
		
		ProjectDto project = projectService.findByProjectNo(project_no);
		List<GithubPullRequestDto> pullRequests = projectService.fetchPullRequests(project_no);
	    model.addAttribute("pullRequests", pullRequests);
		model.addAttribute("project", project);
		
		return "project/projectDetailDevelopmentTab";
	}
	
	@GetMapping("/detail/{project_no}/todo")
	public String detailProjectTodoView(@PathVariable("project_no") Long project_no, Model model) {
		
		ProjectDto project = projectService.findByProjectNo(project_no);
		model.addAttribute("project", project);
		
		return "project/projectDetailTodoTab";
	}
	
	@GetMapping("/detail/{project_no}/files")
	public String detailProjectFilesView(@PathVariable("project_no") Long project_no, Model model) {
		
		ProjectDto project = projectService.findByProjectNo(project_no);
		model.addAttribute("project", project);
		
		return "project/projectDetailFilesTab";
	}
	
	@GetMapping("/detail/{project_no}/minutes")
	public String detailProjectMinutesView(@PathVariable("project_no") Long project_no, Model model) {
		
		ProjectDto project = projectService.findByProjectNo(project_no);
		model.addAttribute("project", project);
		
		return "project/projectDetailMinutesTab";
	}
	
	@GetMapping("/create")
	public String createProjectView(Model model) {
	    return "project/projectCreate";
	}
	
	@PostMapping("/create")
	@ResponseBody
	public Map<String, String> createProjectApi(
	        @ModelAttribute ProjectDto projectDto,
	        @RequestParam("employee_no") Long pmEmployeeNo,
	        @RequestParam(value="managerIds", required=false) List<Long> managerIds,
	        @RequestParam(value="participantIds", required=false) List<Long> participantIds) {
		
		Map<String, String> map = new HashMap<>();
	    map.put("res_code", "500");
	    map.put("res_msg", "프로젝트 생성 중 오류가 발생하였습니다. 다시 시도해주세요.");

	    List<ProjectMemberDto> memberDtos = new ArrayList<>();

	    // PM
	    ProjectMemberDto pmDto = ProjectMemberDto.builder()
	            .project_member(Employee.builder().employeeNo(pmEmployeeNo).build())
	            .project_manager("Y")
	            .build();
	    memberDtos.add(pmDto);

	    // 관리자
	    if (managerIds != null) {
	        for (Long managerId : managerIds) {
	            if (managerId.equals(pmEmployeeNo)) continue;
	            ProjectMemberDto managerDto = ProjectMemberDto.builder()
	                    .project_member(Employee.builder().employeeNo(managerId).build())
	                    .is_manager("Y")  // 관리자 역할인 경우
	                    .build();
	            memberDtos.add(managerDto);
	        }
	    }

	    // 참가자
	    if (participantIds != null) {
	        for (Long participantId : participantIds) {
	            ProjectMemberDto participantDto = ProjectMemberDto.builder()
	                    .project_member(Employee.builder().employeeNo(participantId).build())
	                    .is_manager("N")
	                    .build();
	            memberDtos.add(participantDto);
	        }
	    }

	    Long result = projectService.saveProject(projectDto, memberDtos);
	    
	    if(result != 0L) {
	    	map.put("res_code", "200");
		    map.put("res_msg", "프로젝트가 정상적으로 생성 되었습니다.");
		    map.put("res_project_no", result.toString());
	    }

	    return map;
	}

	
	@GetMapping("/selectStructure")
	@ResponseBody
	public List<SeparatorDto> selectStructure() {
	    List<SeparatorDto> list =  employeeService.findDistinctStructureNames();
	    return list;
	}
	
	@GetMapping("/selectEmployees")
	@ResponseBody
	public List<EmployeeDto> getEmployeesByDepartment(@RequestParam(name = "separator_code") String separatorCode) {
	String temp = separatorCode.substring(0,1);
	System.out.println(temp + " | substring 자르기 1글자 나와야해");
	if ("T".equals(temp)) {
		// 팀(소속) 선택한 경우: separatorCode 기준 조회
		return employeeService.findEmployeesByStructureName(separatorCode);
	} else {
		// 부서를 선택한 경우: parentCode 기준 조회
		return employeeService.findEmployeesByParentCode(separatorCode);
	}
}

	
}
