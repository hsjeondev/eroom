package com.eroom.directory.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.directory.dto.DirectoryBookmarkDto;
import com.eroom.directory.dto.DirectoryMemoDto;
import com.eroom.directory.entity.DirectoryBookmark;
import com.eroom.directory.entity.DirectoryMemo;
import com.eroom.directory.service.DirectoryBookmarkService;
import com.eroom.directory.service.DirectoryMemoService;
import com.eroom.employee.entity.Employee;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DirectoryBookmarkController {

	private final DirectoryBookmarkService directoryBookmarkService;
	
	// 내 북마크 조회
	@GetMapping("/directoryBookmark/{id}")
	@ResponseBody
	public Map<String, String> searchMyBookmark(@PathVariable("id") Long targetNo, Model model, Authentication authentication){
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetail.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "북마크 조회 실패");
		map.put("memo", "");
		
		List<DirectoryBookmark> directoryBookmark = directoryBookmarkService.selectTargetBookmark(employeeNo, targetNo);
		
		
		if(!directoryBookmark.isEmpty()) {
			map.put("res_code", "200");
			map.put("res_msg", "저장된 북마크 없음");
			if(directoryBookmark.get(directoryBookmark.size() - 1).getDirectoryBookmarkYn() != null) {
				String bookmarkYn = directoryBookmark.get(directoryBookmark.size() - 1).getDirectoryBookmarkYn();
				map.put("res_msg", "북마크 조회 성공");
				map.put("bookmark", bookmarkYn);
//				System.out.println(bookmarkYn);
			}
		}
		
		
		return map;
	}
	
	// 북마크 변경
	@PostMapping("/directoryBookmark/change")
	@ResponseBody
	public Map<String, String> createMemo(@RequestBody DirectoryBookmarkDto dto, Authentication authentication){
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		Employee employee = employeeDetail.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "북마크 저장 실패");
		
//		System.out.println("주소록번호 체크 : " + dto.getDirectory_no());
//		System.out.println("메모 체크 : " + dto.getDirectory_memo_content());
		DirectoryBookmark result = directoryBookmarkService.changeBookmark(employee, dto);
		
		if(result != null) {
			map.put("targetEmployeeNo", String.valueOf(result.getDirectory().getEmployee().getEmployeeNo()));
			map.put("res_code", "200");
			map.put("res_msg", "북마크 저장 성공");
		}
		
		return map;
	}
	
}
