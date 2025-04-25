package com.eroom.project.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.project.service.ProjectTodoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProjectTodoController {
	
	private final ProjectTodoService projectTodoService;

	@PostMapping("/projectTodo/addList")
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
	
}
