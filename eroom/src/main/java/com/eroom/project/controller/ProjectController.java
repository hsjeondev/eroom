package com.eroom.project.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.drive.dto.DriveDto;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;
import com.eroom.project.dto.GithubPullRequestDto;
import com.eroom.project.dto.ProjectDto;
import com.eroom.project.dto.ProjectMeetingMinuteDto;
import com.eroom.project.dto.ProjectMemberDto;
import com.eroom.project.dto.ProjectTodoElementDto;
import com.eroom.project.dto.ProjectTodoListDto;
import com.eroom.project.service.ProjectMeetingMinuteService;
import com.eroom.project.service.ProjectService;
import com.eroom.project.service.ProjectTodoService;
import com.eroom.security.EmployeeDetails;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
	
	private final EmployeeService employeeService;
	private final ProjectService projectService;
	private final ProjectTodoService projectTodoService;
	private final ProjectMeetingMinuteService projectMeetingMinuteService;

	@GetMapping("/all")
	public String allProjectView(Model model) {
		
		List<ProjectDto> projectList = projectService.findAllProject();
		Long projectCount = projectService.getAllProjectCount();
		
		model.addAttribute("projectList", projectList);
		model.addAttribute("projectCount", projectCount);
		
		return "project/allProject";
	}
	
	@GetMapping("/doing")
	public String doingProjectView(Model model) {
		
		List<ProjectDto> projectList = projectService.findAllProjectsByProceed("진행 중");
		Long projectCount = projectService.countAllProjectsByProceed("진행 중");
		
		model.addAttribute("projectList", projectList);
		model.addAttribute("projectCount", projectCount);
		
		return "project/doingProject";
	}
	
	@GetMapping("/done")
	public String doneProjectView(Model model) {
		
		List<ProjectDto> projectList = projectService.findAllProjectsByProceed("완료");
		Long projectCount = projectService.countAllProjectsByProceed("완료");
		
		model.addAttribute("projectList", projectList);
		model.addAttribute("projectCount", projectCount);
		
		return "project/doneProject";
	}
	
	@GetMapping("/hold")
	public String holdProjectView(Model model) {
		
		List<ProjectDto> projectList = projectService.findAllProjectsByProceed("보류");
		Long projectCount = projectService.countAllProjectsByProceed("보류");
		
		model.addAttribute("projectList", projectList);
		model.addAttribute("projectCount", projectCount);
		
		return "project/holdProject";
	}
	
	@GetMapping("/yet")
	public String yetProjectView(Model model) {
		
		List<ProjectDto> projectList = projectService.findAllProjectsByProceed("진행 예정");
		Long projectCount = projectService.countAllProjectsByProceed("진행 예정");
		
		model.addAttribute("projectList", projectList);
		model.addAttribute("projectCount", projectCount);
		
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

	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    Employee employee = employeeDetails.getEmployee();

	    ProjectDto project = projectService.findByProjectNo(project_no);
	    List<ProjectMemberDto> projectMembers = projectService.findByProjectMembersNo(project_no);

	    boolean isProjectMember = false;
	    boolean isManager = false;

	    for (ProjectMemberDto member : projectMembers) {
	        if (member.getProject_member().getEmployeeNo().equals(employee.getEmployeeNo())) {
	            isProjectMember = true;

	            if ("Y".equals(member.getProject_manager()) || "Y".equals(member.getIs_manager())) {
	                isManager = true;
	            }
	            break;
	        }
	    }

	    // 최근 파일 5개 조회
	    List<DriveDto> recentFiles = projectService.findRecentFilesByProject(project_no);
	    model.addAttribute("fileList", recentFiles);
	    // 최근 회의록 5개 조회
	    List<ProjectMeetingMinuteDto> recentMinutes = projectService.findRecentMinutesByProject(project_no);
	    model.addAttribute("minutes", recentMinutes);
	    
	    model.addAttribute("project", project);
	    model.addAttribute("description", project.getDescription().replace("\n", "<br>"));
	    model.addAttribute("isProjectMember", isProjectMember);
	    model.addAttribute("isManager", isManager);

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
	    
	    // 로그인한 사용자 정보 가져오기
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    Long currentEmployeeNo = employeeDetails.getEmployee().getEmployeeNo();

	    ProjectDto project = projectService.findByProjectNo(project_no);
	    List<ProjectTodoListDto> allLists = projectTodoService.findByProjectNoWithElementCount(project_no);

	    // isMember / isManager 판별
	    boolean isMember = false;
	    boolean isManager = false;

	    for (ProjectMemberDto member : project.getProject_members()) {
	        if ("Y".equals(member.getVisible_yn()) &&
	            member.getProject_member().getEmployeeNo().equals(currentEmployeeNo)) {
	            isMember = true;
	            if ("Y".equals(member.getProject_manager()) || "Y".equals(member.getIs_manager())) {
	                isManager = true;
	            }
	            break;
	        }
	    }

	    // 할 일 리스트 필터링
	    List<ProjectTodoListDto> projectTodoList = new ArrayList<>();

	    for (ProjectTodoListDto list : allLists) {
	        if ("Y".equals(list.getVisible_yn())) {
	            List<ProjectTodoElementDto> visibleElements = new ArrayList<>();

	            for (ProjectTodoElementDto element : list.getProjectTodoElements()) {
	                if ("Y".equals(element.getVisible_yn())) {
	                    int totalCount = 0;
	                    int completedCount = 0;

	                    if (element.getTodo_element_details() != null) {
	                        for (var detail : element.getTodo_element_details()) {
	                            if ("Y".equals(detail.getVisible_yn())) {
	                                totalCount++;
	                                if ("Y".equals(detail.getStatus())) {
	                                    completedCount++;
	                                }
	                            }
	                        }
	                    }

	                    element.setTotalCount(totalCount);
	                    element.setCompletedCount(completedCount);
	                    visibleElements.add(element);
	                }
	            }

	            list.setProjectTodoElements(visibleElements);
	            projectTodoList.add(list);
	        }
	    }

	    model.addAttribute("project", project);
	    model.addAttribute("projectTodoList", projectTodoList);
	    model.addAttribute("isMember", isMember);   // 🔥 추가
	    model.addAttribute("isManager", isManager); // 🔥 추가

	    return "project/projectDetailTodoTab";
	}





	
	@GetMapping("/detail/{project_no}/files")
	public String detailProjectFilesView(
	    @PathVariable("project_no") Long projectNo,
	    @RequestParam(name = "category", defaultValue = "0") String category,
	    Model model
	) {
	    // 현재 로그인 사용자 정보
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    Long currentEmployeeNo = employeeDetails.getEmployee().getEmployeeNo();

	    // separatorCode 매핑
	    String base = "FL006";
	    String separatorCode = switch (category) {
	        case "1" -> base + "1";
	        case "2" -> base + "2";
	        case "3" -> base + "3";
	        default  -> base;
	    };

	    // 프로젝트 정보와 파일 목록
	    List<DriveDto> fileList = projectService.findProjectFiles(separatorCode, projectNo);
	    ProjectDto project = projectService.findByProjectNo(projectNo);

	    // 현재 사용자가 프로젝트 멤버인지 확인
	    boolean isMember = false;
	    for (ProjectMemberDto member : project.getProject_members()) {
	        if ("Y".equals(member.getVisible_yn()) &&
	            member.getProject_member().getEmployeeNo().equals(currentEmployeeNo)) {
	            isMember = true;
	            break;
	        }
	    }

	    model.addAttribute("project", project);
	    model.addAttribute("fileList", fileList);
	    model.addAttribute("selectedCategory", category);
	    model.addAttribute("isMember", isMember); // 🔥 추가된 부분

	    return "project/projectDetailFilesTab";
	}


	@GetMapping("/detail/{project_no}/minutes")
	public String detailProjectMinutesView(@PathVariable("project_no") Long project_no, Model model) {
	    // 로그인 사용자 정보
	    EmployeeDetails userDetails = (EmployeeDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    Long employeeNo = userDetails.getEmployee().getEmployeeNo();

	    // 프로젝트 정보 및 회의록 목록
	    ProjectDto project = projectService.findByProjectNo(project_no);
	    List<ProjectMeetingMinuteDto> minutes = projectMeetingMinuteService.getMeetingMinutesByProject(project_no);

	    // 프로젝트 참여 여부 확인
	    boolean isProjectMember = projectService.isProjectMember(project_no, employeeNo);

	    model.addAttribute("project", project);
	    model.addAttribute("minutes", minutes);
	    model.addAttribute("isProjectMember", isProjectMember);

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
	
	@GetMapping("/{project_no}/update")
	public String updateProjectView(@PathVariable("project_no") Long projectNo, Model model, HttpServletResponse response) throws IOException {
	    
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    Long currentEmployeeNo = employeeDetails.getEmployee().getEmployeeNo();

	    ProjectDto project = projectService.findByProjectNo(projectNo);

	    Long pmId = null;
	    String pmName = null;
	    List<Long> managerIds = new ArrayList<>();
	    List<String> managerNames = new ArrayList<>();
	    List<Long> participantIds = new ArrayList<>();
	    List<String> participantNames = new ArrayList<>();

	    boolean hasAuthority = false;

	    for (ProjectMemberDto dto : project.getProject_members()) {
	        if (!"Y".equals(dto.getVisible_yn())) continue;

	        Long memberId = dto.getProject_member().getEmployeeNo();

	        // PM 확인
	        if ("Y".equals(dto.getProject_manager())) {
	            pmId = memberId;
	            pmName = dto.getProject_member().getEmployeeName();
	        }

	        // 권한 확인: PM이거나 is_manager == "Y"
	        if (memberId.equals(currentEmployeeNo)) {
	            if ("Y".equals(dto.getProject_manager()) || "Y".equals(dto.getIs_manager())) {
	                hasAuthority = true;
	            }
	        }

	        // 관리자 / 참가자 분류
	        if ("Y".equals(dto.getIs_manager())) {
	            managerIds.add(memberId);
	            managerNames.add(dto.getProject_member().getEmployeeName());
	        } else if ("N".equals(dto.getIs_manager())) {
	            participantIds.add(memberId);
	            participantNames.add(dto.getProject_member().getEmployeeName());
	        }
	    }

	    if (!hasAuthority) {
	        response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
	        return null;
	    }

	    model.addAttribute("pmId", pmId);
	    model.addAttribute("pmName", pmName);
	    model.addAttribute("managerIds", managerIds);
	    model.addAttribute("managerNames", String.join(", ", managerNames));
	    model.addAttribute("participantIds", participantIds);
	    model.addAttribute("participantNames", String.join(", ", participantNames));
	    model.addAttribute("project", project);

	    return "project/projectUpdate";
	}



	
	@PostMapping("/{project_no}/update")
	@ResponseBody
	public Map<String, String> updateProjectApi(
	        @PathVariable("project_no") Long projectNo,
	        @ModelAttribute ProjectDto projectDto,
	        @RequestParam("pmId") Long pmEmployeeNo,
	        @RequestParam(value = "managerIds", required = false) List<Long> managerIds,
	        @RequestParam(value = "participantIds", required = false) List<Long> participantIds) {

	    Map<String, String> map = new HashMap<>();
	    map.put("res_code", "500");
	    map.put("res_msg", "프로젝트 수정 중 오류가 발생하였습니다. 다시 시도해주세요.");

	    // DTO에도 projectNo 설정 (필요하다면)
	    projectDto.setProject_no(projectNo);

	    List<ProjectMemberDto> memberDtos = new ArrayList<>();

	    // 1) PM
	    ProjectMemberDto pmDto = ProjectMemberDto.builder()
	            .project_member(Employee.builder().employeeNo(pmEmployeeNo).build())
	            .project_manager("Y")
	            .build();
	    memberDtos.add(pmDto);

	    // 2) 관리자
	    if (managerIds != null) {
	        for (Long managerId : managerIds) {
	            // PM과 중복인 경우 제외
	            if (managerId.equals(pmEmployeeNo)) continue;
	            ProjectMemberDto managerDto = ProjectMemberDto.builder()
	                    .project_member(Employee.builder().employeeNo(managerId).build())
	                    .is_manager("Y")
	                    .build();
	            memberDtos.add(managerDto);
	        }
	    }

	    // 3) 참가자
	    if (participantIds != null) {
	        for (Long participantId : participantIds) {
	            ProjectMemberDto participantDto = ProjectMemberDto.builder()
	                    .project_member(Employee.builder().employeeNo(participantId).build())
	                    .is_manager("N")
	                    .build();
	            memberDtos.add(participantDto);
	        }
	    }

	    // 서비스 호출: 수정 로직 수행
	    Long result = projectService.updateProject(projectDto, memberDtos);
	    if (result != null && result != 0L) {
	        map.put("res_code", "200");
	        map.put("res_msg", "프로젝트가 정상적으로 수정 되었습니다.");
	        map.put("res_project_no", result.toString());
	    }

	    return map;
	}
	
	@PostMapping("/{project_no}/holding")
	@ResponseBody
	public Map<String, String> holdingProjectApi(@PathVariable("project_no") Long projectNo) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "보류 처리 중 오류가 발생하였습니다. 다시 시도해주세요.");
		
		int result = projectService.holdingProject(projectNo);

		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "보류 처리가 정상적으로 되었습니다.");
		}

		return map;
	}
	
	@PostMapping("/{project_no}/done")
	@ResponseBody
	public Map<String, String> doneProjectApi(@PathVariable("project_no") Long projectNo) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "완료 처리 중 오류가 발생하였습니다. 다시 시도해주세요.");
		
		int result = projectService.doneProject(projectNo);

		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "완료 처리가 정상적으로 되었습니다.");
		}

		return map;
	}
	
	@PostMapping("/projectMypage")
	@ResponseBody
	public Map<String, Object> projectMypageApi() {
	    Map<String, Object> map = new HashMap<>();
	    map.put("res_code", "500");
	    map.put("res_msg", "내 프로젝트를 불러오는 중 오류가 발생하였습니다.");

	    try {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	        Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();

	        List<ProjectDto> doingProject = projectService.getMyDoingProject(employeeNo);
	        List<ProjectDto> doneProject = projectService.getMyDoneProject(employeeNo);

	        map.put("res_code", "200");
	        map.put("res_msg", "내 프로젝트를 성공적으로 불러왔습니다.");
	        map.put("activeProjects", doingProject);
	        map.put("doneProjects", doneProject);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return map;
	}
	
	@PostMapping("/myProjectCount")
	@ResponseBody
	public Map<String, Object> projectMypageCountsApi() {
	    Map<String, Object> map = new HashMap<>();
	    map.put("res_code", "500");
	    map.put("res_msg", "프로젝트 개수를 불러오는 중 오류가 발생하였습니다.");

	    try {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	        Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();

	        int doingCount = projectService.countMyDoingProject(employeeNo); // 진행 중
	        int upcomingCount = projectService.countMyUpcomingProject(employeeNo); // 진행 예정
	        int doneCount = projectService.countMyDoneProject(employeeNo); // 완료
	        
	        map.put("res_code", "200");
	        map.put("res_msg", "프로젝트 개수를 성공적으로 불러왔습니다.");
	        map.put("doingCount", doingCount);
	        map.put("upcomingCount", upcomingCount);
	        map.put("doneCount", doneCount);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return map;
	}

	
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, String> deleteProject(@RequestBody Map<String, Long> request) {
	    Long projectNo = request.get("projectNo");
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
        resultMap.put("res_msg", "삭제 중 오류가 발생했습니다.");
	    
        int result = projectService.deleteProjectById(projectNo);
	        
        if(result > 0) {
        	resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "삭제가 완료되었습니다.");
        }
	        
	    return resultMap;
	}


	
}
