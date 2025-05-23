package com.eroom.project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.project.dto.EditTodoDetailRequest;
import com.eroom.project.dto.ProjectTodoElementDetailDto;
import com.eroom.project.dto.ProjectTodoElementDto;
import com.eroom.project.dto.ProjectTodoListDto;
import com.eroom.project.dto.TodoDetailResponseDto;
import com.eroom.project.service.ProjectTodoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/projectTodo")
public class ProjectTodoController {

	private final ProjectTodoService projectTodoService;
	
	@PostMapping("/deleteTodoElement")
	@ResponseBody
	public Map<String, Object> deleteTodoElement(@RequestBody Map<String, Object> param) {
	    Long todoNo = Long.valueOf(param.get("todoNo").toString());

	    int result = projectTodoService.softDeleteTodoElement(todoNo);

	    Map<String, Object> resultMap = new HashMap<>();
	    if (result > 0) {
	    	resultMap.put("res_code", "200");
	    	resultMap.put("res_msg", "할 일이 성공적으로 삭제되었습니다.");
	    } else {
	    	resultMap.put("res_code", "500");
	    	resultMap.put("res_msg", "할 일을 삭제하는 중 오류가 발생했습니다.");
	    }

	    return resultMap;
	}

	
	@PostMapping("/updateTodoElement")
	@ResponseBody
	public Map<String, String> updateTodoElement(
	    @RequestParam("todo_no") Long todoNo,
	    @RequestParam("todo_title") String todoTitle,
	    @RequestParam("project_todo_list_no") Long listNo,
	    @RequestParam("employee_no") Long employeeNo,
	    @RequestParam(value = "emergency", required = false) String emergency
	) {
	    Map<String, String> map = new HashMap<>();
	    map.put("res_code", "500");
	    map.put("res_msg", "수정 중 오류 발생");

	    // service 호출 예시
	    int result = projectTodoService.updateTodoElement(todoNo, todoTitle, listNo, employeeNo, emergency);

	    if (result > 0) {
	        map.put("res_code", "200");
	        map.put("res_msg", "수정 완료");
	    }

	    return map;
	}

	
	@PostMapping("/findTodoElementOne")
	@ResponseBody
	public Map<String, Object> findTodoElementOne(@RequestBody Map<String, Object> param) {
	    Long todoElementNo = Long.valueOf(param.get("todoElementNo").toString());

	    Map<String, Object> dataMap = projectTodoService.findTodoElementOne(todoElementNo);

	    Map<String, Object> resultMap = new HashMap<>();
	    if (!dataMap.isEmpty()) {
	    	resultMap.put("res_code", "200");
	    	resultMap.put("res_msg", "성공적으로 조회했습니다.");
	    	resultMap.put("res_data", dataMap);
	    } else {
	    	resultMap.put("res_code", "500");
	    	resultMap.put("res_msg", "할 일을 찾을 수 없습니다.");
	    }

	    return resultMap;
	}

	
	@PostMapping("/deleteList")
	@ResponseBody
	public Map<String, String> deleteListOne(@RequestBody Map<String, Object> param) {
		Long projectTodoListNo = Long.valueOf(param.get("projectTodoListNo").toString());
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "리스트 삭제 중 오류가 발생하였습니다.");
		
		int result = projectTodoService.deleteListOne(projectTodoListNo);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "리스트가 정상적으로 삭제 되었습니다.");
		}
		
		return map;
	}
	
	@PostMapping("/updateList")
	@ResponseBody
	public Map<String, String> updateList(
	    @RequestParam("project_todo_list_no") Long projectTodoListNo,
	    @RequestParam("project_no") Long projectNo,
	    @RequestParam("list_name") String listName,
	    @RequestParam("list_color") String listColor,
	    @RequestParam(value = "position", required = false) String position,
	    @RequestParam(value = "standard_list_name", required = false) String standardListId
	) {
	    Map<String, String> map = new HashMap<>();
	    map.put("res_code", "500");
	    map.put("res_msg", "리스트 수정 중 오류가 발생하였습니다.");

	    int result = projectTodoService.updateList(projectTodoListNo, projectNo, listName, listColor, position, standardListId);

	    if (result > 0) {
	        map.put("res_code", "200");
	        map.put("res_msg", "리스트가 정상적으로 수정되었습니다.");
	    }

	    return map;
	}

	
	@PostMapping("/findProjectTodoListOne")
	@ResponseBody
	public Map<String, Object> findProjectTodoListOne(@RequestBody Map<String, Object> param) {
		Long projectTodoListNo = Long.valueOf(param.get("projectTodoListNo").toString());
		Long projectNo = Long.valueOf(param.get("projectNo").toString());
		
		Map<String, String> dataMap = projectTodoService.findProjectTodoListOne(projectTodoListNo);
		
		List<ProjectTodoListDto> projectTodoList = projectTodoService.findByProjectNo(projectNo);
		
		List<Map<String, Object>> simplifiedList = new ArrayList<>();

		for (ProjectTodoListDto dto : projectTodoList) {
		    Map<String, Object> item = new HashMap<>();
		    item.put("project_todo_list_no", dto.getProject_todo_list_no());
		    item.put("list_name", dto.getList_name());
		    item.put("list_sequence", dto.getList_sequence());
		    simplifiedList.add(item);
		}


		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "리스트 하나를 가져오는데 오류가 발생하였습니다.");
		
		if (!dataMap.isEmpty()) {
		    resultMap.put("res_code", "200");
		    resultMap.put("res_msg", "리스트 하나를 정상적으로 가져왔습니다.");
		    resultMap.put("res_data", dataMap);
		    resultMap.put("res_project_todo_list", simplifiedList);
		}

		
		return resultMap;
	}

	@PostMapping("/addList")
	@ResponseBody
	public Map<String, String> addList(
		@RequestParam("project_no") Long projectNo,
	    @RequestParam("list_name") String listName,
	    @RequestParam("list_color") String listColor,
	    @RequestParam(value = "position", required = false) String position,
	    @RequestParam(value = "standard_list_name", required = false) String standardListId
	) {

	    Map<String, String> map = new HashMap<String, String>();
	    map.put("res_code", "500");
	    map.put("res_msg", "리스트 추가 중 오류가 발생하였습니다. 다시 시도해주세요.");
	    
	    int result = projectTodoService.addList(projectNo, listName, listColor, position, standardListId);
	    
	    if(result > 0) {
	    	map.put("res_code", "200");
		    map.put("res_msg", "리스트가 정상적으로 추가 되었습니다.");
	    }
	    
	    return map;
	}
	
	@PostMapping("/updateElement")
	@ResponseBody
	public Map<String, String> updateTodoElement(@RequestBody Map<String, Object> payload) {
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("res_code", "500");
        map.put("res_msg", "이동 중 오류가 발생했습니다.");
	    
	    Long listNo = Long.valueOf(payload.get("listNo").toString());
        Long todoNo = Long.valueOf(payload.get("todoNo").toString());
        int newIndex = Integer.parseInt(payload.get("newIndex").toString());
        
        int result = projectTodoService.updateElementPosition(listNo, todoNo, newIndex);
        
        if(result > 0) {
        	map.put("res_code", "200");
	        map.put("res_msg", "성공적으로 이동되었습니다.");
        }
        	    
	    return map;
	}
	
	@PostMapping("/createTodoElement")
	@ResponseBody
	public Map<String, String> createTodoElement(ProjectTodoElementDto projectTodoElementDto) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "할 일 추가 중 오류가 발생하였습니다.");
		
		int result = projectTodoService.createTodoElement(projectTodoElementDto);
		
		if(result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "할 일이 정상적으로 추가 되었습니다.");
		}
		
		return map;
	}
	
	@PostMapping("/todoElementDetail")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> todoElementDetail(@RequestBody Map<String, Object> param) {
	    Long todoElementNo = Long.valueOf(String.valueOf(param.get("todoElementNo")));
	    Map<String, Object> map = new HashMap<>();

	    ProjectTodoElementDto projectTodoElementDto = projectTodoService.findTodoElement(todoElementNo);

	    if (projectTodoElementDto != null) {

	        List<ProjectTodoElementDetailDto> filteredDetails = new ArrayList<>();
	        if (projectTodoElementDto.getTodo_element_details() != null) {
	            for (ProjectTodoElementDetailDto detail : projectTodoElementDto.getTodo_element_details()) {
	                if ("Y".equals(detail.getVisible_yn())) {
	                    filteredDetails.add(detail);
	                }
	            }
	        }

	        TodoDetailResponseDto responseDto = TodoDetailResponseDto.builder()
	            .todoNo(projectTodoElementDto.getTodo_no())
	            .todoTitle(projectTodoElementDto.getTodo_title())
	            .emergency(projectTodoElementDto.getEmergency())
	            .listName(projectTodoElementDto.getProjectTodolist().getListName())
	            .listNo(projectTodoElementDto.getProjectTodolist().getProjectTodoListNo())
	            .listColor(projectTodoElementDto.getProjectTodolist().getListColor())
	            .employeeName(projectTodoElementDto.getEmployee().getEmployeeName())
	            .employeeNo(projectTodoElementDto.getEmployee().getEmployeeNo())
	            .details(filteredDetails) // 👈 필터링된 리스트 사용
	            .build();

	        map.put("res_code", "200");
	        map.put("res_msg", "할 일을 정상적으로 조회하였습니다.");
	        map.put("res_data", responseDto);
	    } else {
	        map.put("res_code", "500");
	        map.put("res_msg", "할 일 조회 중 오류가 발생하였습니다.");
	    }

	    return ResponseEntity.ok()
	            .contentType(MediaType.APPLICATION_JSON)
	            .body(map);
	}


	@PostMapping("/elementDetailCheck")
	@ResponseBody
	public Map<String, Object> elementDetailCheck(@RequestBody Map<String, Object> param) {
	    Long todoDetailNo = Long.valueOf(param.get("todoDetailNo").toString());
	    String status = param.get("status").toString();

	    Map<String, Object> map = new HashMap<>();
	    map.put("res_code", "500");
	    map.put("res_msg", "상태 업데이트 중 오류가 발생하였습니다.");

	    Map<String, Object> result = projectTodoService.updateElementDetailStatus(todoDetailNo, status);

	    if (!result.isEmpty()) {
	        map.put("res_code", "200");
	        map.put("res_msg", "상태가 정상적으로 업데이트 됐습니다.");
	        map.put("res_data", result);
	    }

	    return map;
	}
	
	@PostMapping("/editTodoElementDetails")
	@ResponseBody
	public Map<String, Object> editTodoElementDetails(@RequestBody EditTodoDetailRequest request) {
	    Map<String, Object> map = new HashMap<>();
	    map.put("res_code", "500");
	    map.put("res_msg", "세부 할 일 수정 중 오류가 발생하였습니다.");
	    
	    try {
	        projectTodoService.editTodoElementDetails(request);

	        // ✅ 여기 추가
	        int totalCount = projectTodoService.countVisibleDetails(request.getTodoElementNo());
	        int completedCount = projectTodoService.countVisibleCompletedDetails(request.getTodoElementNo());

	        map.put("res_code", "200");
	        map.put("res_msg", "세부 할 일 수정이 완료되었습니다.");
	        map.put("total_count", totalCount);
	        map.put("completed_count", completedCount);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return map;
	}


	


	


	
}
