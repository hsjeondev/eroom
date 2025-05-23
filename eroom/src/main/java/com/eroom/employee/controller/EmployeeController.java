package com.eroom.employee.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.service.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeController {
	private final EmployeeService employeeService;

	@GetMapping("/login")
	public String loginView(HttpServletRequest request, Model model) {
	    String errorMsg = (String) request.getSession().getAttribute("loginErrorMsg");
	    
//	    System.out.println("errorMsg " + errorMsg);

	    if (errorMsg != null) {
	        model.addAttribute("error", "true");
	        model.addAttribute("errorMsg", errorMsg);
	        request.getSession().removeAttribute("loginErrorMsg");
	    }

	    return "login";
	}


	@GetMapping("/admin")
	public String adminView() {
		return "admin";
	}

	@GetMapping("/employes")
	@ResponseBody
	public List<EmployeeDto> getEmployeesByDepartment(@RequestParam(name = "separator_code") String separatorCode) {
		String temp = separatorCode.substring(0, 1);
		if ("T".equals(temp)) {
			// 팀(소속) 선택한 경우: separatorCode 기준 조회
			return employeeService.findEmployeesByStructureName(separatorCode);
		} else {
			// 부서를 선택한 경우: parentCode 기준 조회
			return employeeService.findEmployeesByParentCode(separatorCode);
		}

	}
	
	@GetMapping("/session/employeeNo")
	@ResponseBody
	public Long getEmployeeNo(HttpSession session) {
	    Object employeeNo = session.getAttribute("employeeNo");
	    if (employeeNo != null) {
	        return Long.parseLong(employeeNo.toString());
	    }
	    return null;
	}

}
