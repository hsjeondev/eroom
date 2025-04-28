package com.eroom.project.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.project.dto.ProjectTodoElementDto;
import com.eroom.project.service.ProjectTodoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/projectTodo")
public class ProjectTodoController {
	
	private final ProjectTodoService projectTodoService;

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
	

	
}
