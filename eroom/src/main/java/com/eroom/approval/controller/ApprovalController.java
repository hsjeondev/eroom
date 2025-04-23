package com.eroom.approval.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.approval.dto.ApprovalDto;
import com.eroom.approval.dto.ApprovalFormatDto;
import com.eroom.approval.dto.ApprovalLineDto;
import com.eroom.approval.dto.ApprovalRequestDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalFormat;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.service.ApprovalFormatService;
import com.eroom.approval.service.ApprovalLineService;
import com.eroom.approval.service.ApprovalService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.EmployeeService;
import com.eroom.employee.service.StructureService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class ApprovalController {
	
	private final ApprovalService approvalService;
	private final ApprovalLineService approvalLineService;
	private final ApprovalFormatService approvalFormatService;
	private final StructureService structureService;
	private final EmployeeService employeeService;
	
	// 내가 올린 결재 리스트 조회
	@GetMapping("/approval/myRequestedApprovals")
	public String selectMyRequestedApprovalsList(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		model.addAttribute("employee", employee);
		// 내가 올린 approval 리스트 조회
		List<Approval> temp = approvalService.getMyRequestedApprovals(employee.getEmployeeNo(), "Y");
		List<ApprovalDto> resultList = new ArrayList<ApprovalDto>();
		Map<Long, List<ApprovalLineDto>> approvalLineMap = new HashMap<Long, List<ApprovalLineDto>>();
		for (Approval approval : temp) {
			// approval 리스트의 approval_no를 사용해서 approval_line 리스트 조회
			List<ApprovalLine> temp2 = approvalLineService.getApprovalLineByApprovalNo(approval.getApprovalNo());
			List<ApprovalLineDto> approvalLineDtoList = new ArrayList<ApprovalLineDto>();
			for (ApprovalLine approvalLine : temp2) {
				ApprovalLineDto approvalLineDto = new ApprovalLineDto().toDto(approvalLine);
				approvalLineDtoList.add(approvalLineDto);
			}
			// approval_line 리스트를 approval_no를 키로 하는 맵에 저장
			approvalLineMap.put(approval.getApprovalNo(), approvalLineDtoList);
			
			
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(approval);
			resultList.add(dto);
		}
		model.addAttribute("approvalLineMap", approvalLineMap);
		model.addAttribute("resultList", resultList);
		
		
		return "/approval/myRequestedApprovals";
	}
	// 결재 생성 페이지 진입
	@GetMapping("/approval/create")
	public String selectApprovalCreate(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		model.addAttribute("employee", employee);
//		System.out.println("employee : " + employee);
		// 결재 양식 리스트 조회
		List<ApprovalFormat> temp = approvalFormatService.getApprovalFormatList();
		List<ApprovalFormatDto> approvalFormatList = new ArrayList<ApprovalFormatDto>();
		for (ApprovalFormat format : temp) {
			ApprovalFormatDto dto = new ApprovalFormatDto();
			dto = dto.toDto(format);
			approvalFormatList.add(dto);
		}
		model.addAttribute("approvalFormatList", approvalFormatList);
		// 부서 정보 조회
		Structure structure = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(employee.getStructure().getParentCode());
        String departmentName = structure != null ? structure.getCodeName() : "-";
        model.addAttribute("departmentName", departmentName);
        // 현재 날짜
        model.addAttribute("now", DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDateTime.now()));
        // 부서 코드네임 조회
		List<SeparatorDto> structureList = employeeService.findDistinctStructureNames();
		model.addAttribute("structureList", structureList);
		return "/approval/create";
	}
	
	// 결재 생성 결재라인 추가를 위한 조회
	@GetMapping("/approval/employes")
	@ResponseBody
	public List<EmployeeDto> getEmployeesByDepartment(@RequestParam(name = "separator_code") String separatorCode) {
		String temp = separatorCode.substring(0,1);
//		System.out.println(temp + " | substring 자르기 1글자 나와야해");
		if ("T".equals(temp)) {
			// 팀(소속) 선택한 경우: separatorCode 기준 조회
			return employeeService.findEmployeesByStructureName(separatorCode);
		} else {
			// 부서를 선택한 경우: parentCode 기준 조회
			return employeeService.findEmployeesByParentCode(separatorCode);
		}
	}
	
	// 결재 생성 양식 리스트 조회
	@PostMapping("/approval/format")
	@ResponseBody
	public Map<String, String> selectApprovalFormat(@RequestBody ApprovalFormatDto dto) {
		// 결재 양식 조회
		ApprovalFormat approvalFormat = approvalFormatService.getApprovalFormat(dto.getApproval_format_no());
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "양식 조회 실패");
		if(approvalFormat != null) {
			map.put("res_code", "200");
			map.put("res_msg", "양식 조회 성공");
			ApprovalFormatDto approvalFormatDto = new ApprovalFormatDto().toDto(approvalFormat);
			map.put("approvalFormatContent", approvalFormatDto.getApproval_format_content());
			map.put("approvalFormatTitle", approvalFormatDto.getApproval_format_title());
		}
		return map;
	}
	
	// 결재 생성 라인 추가
	@PostMapping("/approval/addApprovalLine")
	@ResponseBody
	public Map<String, String> addApprovalLine(@RequestBody ApprovalLineDto dto) {
		// 결재 라인 추가
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "결재 라인 추가 실패");
//		if() {
//			map.put("res_code", "200");
//			map.put("res_msg", "결재 라인 추가 성공");
//		}
		return map;
		
	}
	
	// 결재 생성 Create
	@PostMapping("/approval/create")
	@ResponseBody
	public Map<String, String> createApproval(@RequestBody ApprovalRequestDto dto, Authentication authentication) {
		Map<String, String> map = new HashMap<String, String>();

		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		int result = approvalService.createApproval(dto, employeeNo);
		
		
		map.put("res_code", "500");
		map.put("res_msg", "결재 생성 실패");
		if (result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "결재 생성 성공");
		}
		return map;

	}
	
	@GetMapping("/approval/detail/{approvalNo}")
	public String selectApprovalDetail(@PathVariable("approvalNo") Long approvalNo, Model model, Authentication authentication) {
		
		
		
		// 선택한 결재 번호로 결재 정보 조회
		Approval approval = approvalService.selectApprovalByApprovalNo(approvalNo);
		// 결재 정보가 없으면 404 에러 페이지로 이동
		if (approval == null) {
		    return "redirect:/error/404";
		}
		ApprovalDto approvalDto = new ApprovalDto().toDto(approval);
		model.addAttribute("approval", approvalDto);
		
		// 로그인한 사용자 정보 조회
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		model.addAttribute("employee", employee);
		
		// 결재 라인 정보 조회
		List<ApprovalLine> temp = approvalLineService.getApprovalLineByApprovalNo(approvalNo);
		List<ApprovalLineDto> approvalLineDtoList = new ArrayList<ApprovalLineDto>();
		for (ApprovalLine approvalLine : temp) {
			ApprovalLineDto approvalLineDto = new ApprovalLineDto().toDto(approvalLine);
			approvalLineDtoList.add(approvalLineDto);
		}
		model.addAttribute("approvalLineList", approvalLineDtoList);
		
		// 권한 리스트 조회
		Set<Long> authorityList = new HashSet<Long>();
		for(ApprovalLine t : temp) {
			// 결재자, 합의자, 참조자 중 결재라인에 있는 사람들
			authorityList.add(t.getEmployee().getEmployeeNo());
		}
		// 기안자
		authorityList.add(approval.getEmployee().getEmployeeNo());
		
		// 결재 관련 인원 + 관리자만 접근 가능
		if (!authorityList.contains(employee.getEmployeeNo()) && !employee.getEmployeeName().contains("admin")) {
	        return "redirect:/error/403";
	    }
		
		return "/approval/detail";
	}
	
	@PostMapping("/approval/{approvalNo}/delete")
	@ResponseBody
	public Map<String, String> deleteApproval(@PathVariable("approvalNo") Long approvalNo, Model model,
			Authentication authentication) {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "결재 삭제 실패");
		
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		// 내가 올린 결재인지 확인
		Approval approval = approvalService.selectApprovalByApprovalNo(approvalNo);
		Boolean isMyApproval = false;
		if (approval.getEmployee().getEmployeeNo() == employeeNo) {
			isMyApproval = true;
		}
		if(isMyApproval == false && employee.getEmployeeName().contains("admin") == false) {
			map.put("res_code", "403");
			map.put("res_msg", "결재 삭제 실패(권한 없음)");
			return map;
		}
		
		// 결재 라인들의 상태 조회 -> 결재자가 아무도 결재 승인을 안 했다면 삭제 프로세스 진행
		List<ApprovalLine> approvalLineList = approval.getApprovalLines();
		Map<Integer, String> approvalLineStatusMap = new HashMap<Integer, String>();
		Boolean isApproval = false;
		for(ApprovalLine app : approvalLineList) {
			approvalLineStatusMap.put(app.getApprovalLineStep(), app.getApprovalLineStatus());
		}
		if(approvalLineStatusMap.containsKey(1) && approvalLineStatusMap.get(1).equals("S")) {
			isApproval = true;
		} else if(!approvalLineStatusMap.containsKey(1)) {
			isApproval = true;
		}
		
		if(isApproval) {
			// 결재 삭제
			int result = approvalService.updateVisibleYn(approvalNo);
			
			if (result > 0) {
				map.put("res_code", "200");
				map.put("res_msg", "결재 삭제 성공");
			} else {
				map.put("res_code", "500");
				map.put("res_msg", "결재 삭제 실패(서버 오류)");
			}
		} else {
			map.put("res_code", "500");
			map.put("res_msg", "결재 삭제 실패(결재가 진행중입니다.)");
		}
		return map;
	}
	
	@PostMapping("/approval/{fallBackNo}/fallBack")
	@ResponseBody
	public Map<String, String> fallBackApproval(@PathVariable("fallBackNo") Long approvalNo, Model model,
			Authentication authentication) {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("res_code", "500");
		map.put("res_msg", "결재 회수 실패");
		
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
		// 내가 올린 결재인지 확인
		Approval approval = approvalService.selectApprovalByApprovalNo(approvalNo);
		Boolean isMyApproval = false;
		if (approval.getEmployee().getEmployeeNo() == employeeNo) {
			isMyApproval = true;
		}
		if(isMyApproval == false && employee.getEmployeeName().contains("admin") == false) {
			map.put("res_code", "403");
			map.put("res_msg", "결재 회수 실패(권한 없음)");
			return map;
		}
		
		// 결재 라인들의 상태 조회 -> 결재자가 아무도 결재 승인을 안 했다면 삭제 프로세스 진행
		List<ApprovalLine> approvalLineList = approval.getApprovalLines();
		Map<Integer, String> approvalLineStatusMap = new HashMap<Integer, String>();
		Boolean isApproval = false;
		for(ApprovalLine app : approvalLineList) {
			approvalLineStatusMap.put(app.getApprovalLineStep(), app.getApprovalLineStatus());
		}
		if(approvalLineStatusMap.containsKey(1) && approvalLineStatusMap.get(1).equals("S")) {
			isApproval = true;
		} else if(!approvalLineStatusMap.containsKey(1)) {
			isApproval = true;
		}
		
		if(isApproval) {
			// 결재 회수
			int result = approvalService.updateApprovalStatus(approvalNo);
			
			if (result > 0) {
				map.put("res_code", "200");
				map.put("res_msg", "결재 회수 성공");
			} else {
				map.put("res_code", "500");
				map.put("res_msg", "결재 회수 실패(서버 오류)");
			}
		} else {
			map.put("res_code", "500");
			map.put("res_msg", "결재 회수 실패(결재가 진행중입니다.)");
		}
		return map;
	}
	
	// 리팩토링
//	@GetMapping("/approval/receivedApprovals")
//	public String selectReceivedApprovalsList(Model model, Authentication authentication) {
//
//	    // 1. 로그인 정보
//	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
//	    Employee employee = employeeDetails.getEmployee();
//	    Long employeeNo = employee.getEmployeeNo();
//	    model.addAttribute("employee", employee);
//
//	    // 2. 내가 속한 결재라인 조회
//	    List<ApprovalLine> myLines = approvalLineService.getApprovalLineByEmployeeNo(employeeNo);
//	    Map<Long, List<ApprovalLineDto>> approvalLineMap = new HashMap<>();
//	    List<ApprovalDto> resultApprovalListDto = new ArrayList<>();
//
//	    for (ApprovalLine myLine : myLines) {
//	        Approval approval = myLine.getApproval();
//
//	        // 3. 해당 결재문서가 visible 상태인지 확인
//	        List<Approval> visibleApprovals = approvalService.getApprovalListByApprovalNo(approval.getApprovalNo(), "Y");
//	        if (visibleApprovals == null || visibleApprovals.isEmpty()) continue;
//
//	        for (Approval a : visibleApprovals) {
//
//	            // 4. 결재라인 Map 저장 (ApprovalNo 기준)
//	            List<ApprovalLineDto> lineDtos = a.getApprovalLines().stream()
//	                .map(line -> new ApprovalLineDto().toDto(line))
//	                .collect(Collectors.toList());
//	            approvalLineMap.put(a.getApprovalNo(), lineDtos);
//
//	            // 5. 내가 결재할 차례인지 판단
//	            List<ApprovalLine> lines = a.getApprovalLines();
//	            for (int i = 0; i < lines.size(); i++) {
//	                boolean isMyTurn = false;
//
//	                if (i == 0) {
//	                    if (lines.get(i).getApprovalLineStep() >= 1
//	                        && lines.get(i).getEmployee().getEmployeeNo().equals(employeeNo)) {
//	                        isMyTurn = true;
//	                    }
//	                } else {
//	                    if (lines.get(i).getApprovalLineStep() >= 1
//	                        && lines.get(i - 1).getApprovalLineStatus().equals("A")
//	                        && lines.get(i).getEmployee().getEmployeeNo().equals(employeeNo)) {
//	                        isMyTurn = true;
//	                    }
//	                }
//
//	                if (isMyTurn) {
//	                    Approval fullApproval = approvalService.selectApprovalByApprovalNo(a.getApprovalNo());
//	                    ApprovalDto dto = new ApprovalDto().toDto(fullApproval);
//	                    resultApprovalListDto.add(dto);
//	                    break; // 이미 추가했으면 중복 방지
//	                }
//	            }
//	        }
//	    }
//
//	    model.addAttribute("resultList", resultApprovalListDto);
//	    model.addAttribute("approvalLineMap", approvalLineMap);
//
//	    return "/approval/receivedApprovals";
//	}

	@GetMapping("/approval/receivedApprovals")
	public String selectReceivedApprovalsList(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		// 내가 결재 라인에 있는 approval 리스트 조회
		List<ApprovalLine> var1Entity = approvalLineService.getApprovalLineByEmployeeNo(employeeNo);
		Map<Long, List<ApprovalLineDto>> var1Map = new HashMap<Long, List<ApprovalLineDto>>();
		Approval temp2 = null;
		List<Approval> resultApprovalList = new ArrayList<Approval>();
		List<ApprovalDto> resultApprovalListDto = new ArrayList<ApprovalDto>();
		
		for (ApprovalLine a : var1Entity) {
			
			
			
			// 결재라인용 - 내가 결재 라인에 있는 approval들의 결재번호로 approval 리스트 조회
			List<Approval> approvalList = approvalService.getApprovalListByApprovalNo(a.getApproval().getApprovalNo(), "Y");
			if (approvalList != null) {
				// 결재라인용 - 결재라인에 있는 결재번호로 approval 리스트 entity -> Dto 변환
				for (Approval approval : approvalList) {
					List<ApprovalLine> var1 = approvalLineService.getApprovalLineByApprovalNo(approval.getApprovalNo());
					List<ApprovalLineDto> tempDtoList = new ArrayList<>();
				    for(int i = 0; i < var1.size(); i++) {
				    	tempDtoList.add(new ApprovalLineDto().toDto(var1.get(i)));
				    }
				    var1Map.put(approval.getApprovalNo(), tempDtoList);
					
				    // 결재 리스트옹 - 내가 해당 결재의 현재 순번이 맞는 결재들만 보내기
					List<ApprovalLine> temp = approval.getApprovalLines();
					for(int i = 0; i < temp.size(); i++) {
						Boolean bool = false;
						if (i > 0) {
							if (temp.get(i).getApprovalLineStep() >= 1 && temp.get(i - 1).getApprovalLineStatus().equals("A") && temp.get(i).getEmployee().getEmployeeNo() == employeeNo) {
								bool = true;
							}
						} else if (i == 0) {
							if (temp.get(i).getApprovalLineStep() >= 1 && temp.get(i).getEmployee().getEmployeeNo() == employeeNo) {
								bool = true;
							}
						}
						if(bool && !approval.getApprovalStatus().equals("F")) {
							temp2 = approvalService.selectApprovalByApprovalNo(temp.get(i).getApproval().getApprovalNo());
							resultApprovalList.add(temp2);
						}
						
				    }
				}
			}
		}
		// 결재 리스트옹 - 내가 결재 순서인 approval들을 Dto로 변환
		for (Approval t : resultApprovalList) {
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(t);
			resultApprovalListDto.add(dto);
		}
		model.addAttribute("resultList", resultApprovalListDto);
		model.addAttribute("approvalLineMap", var1Map);
		
		return "/approval/receivedApprovals";
	}
//	백업용
//	@GetMapping("/approval/receivedApprovals")
//	public String selectReceivedApprovalsList(Model model, Authentication authentication) {
//		// 로그인한 사용자 정보 가져오기
//		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
//		Employee employee = employeeDetails.getEmployee();
//		Long employeeNo = employee.getEmployeeNo();
//		model.addAttribute("employee", employee);
//		// 내가 결재 라인에 있는 approval 리스트 조회
//		List<ApprovalLine> var1Entity = approvalLineService.getApprovalLineByEmployeeNo(employeeNo);
//		List<ApprovalDto> approvalDtoList = new ArrayList<ApprovalDto>();
//		List<ApprovalLineDto> var1Dto = new ArrayList<ApprovalLineDto>();
//		Map<Long, List<ApprovalLineDto>> var1Map = new HashMap<Long, List<ApprovalLineDto>>();
//		
//		for (ApprovalLine a : var1Entity) {
//			// 내가 해당 결재의 현재 순번이 맞는지 확인
//			
//			
//			// 내가 결재 라인에 있는 approval들의 결재번호로 approval 리스트 조회
//			List<Approval> approvalList = approvalService.getApprovalListByApprovalNo(a.getApproval().getApprovalNo(), "Y");
//			if (approvalList != null) {
//				// 결재라인에 있는 결재번호로 approval 리스트 entity -> Dto 변환
//				for (Approval approval : approvalList) {
//					List<ApprovalLine> var1 = approvalLineService.getApprovalLineByApprovalNo(approval.getApprovalNo());
//					for (ApprovalLine approvalLine : var1) {
//						ApprovalLineDto approvalLineDto = new ApprovalLineDto().toDto(approvalLine);
//						var1Dto.add(approvalLineDto);
//					}
//					
//					
//					var1Map.put(approval.getApprovalNo(), var1Dto);
//					ApprovalDto dto = new ApprovalDto();
//					dto = dto.toDto(approval);
//					approvalDtoList.add(dto);
//				}
//			}
//		}
//		
//		model.addAttribute("approvalLineMap", var1Map);
//		model.addAttribute("resultList", approvalDtoList);		
//		
//		return "/approval/receivedApprovals";
//	}
	@GetMapping("/approval/agreementApprovals")
	public String selectAgreementApprovalsList(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		// 내가 결재 라인에 있는 approval 리스트 조회
		List<ApprovalLine> var1Entity = approvalLineService.getApprovalLineByEmployeeNo(employeeNo);
		Map<Long, List<ApprovalLineDto>> var1Map = new HashMap<Long, List<ApprovalLineDto>>();
		Approval temp2 = null;
		List<Approval> resultApprovalList = new ArrayList<Approval>();
		List<ApprovalDto> resultApprovalListDto = new ArrayList<ApprovalDto>();
		
		for (ApprovalLine a : var1Entity) {
			
			
			
			// 결재라인용 - 내가 결재 라인에 있는 approval들의 결재번호로 approval 리스트 조회
			List<Approval> approvalList = approvalService.getApprovalListByApprovalNo(a.getApproval().getApprovalNo(), "Y");
			if (approvalList != null) {
				// 결재라인용 - 결재라인에 있는 결재번호로 approval 리스트 entity -> Dto 변환
				for (Approval approval : approvalList) {
					List<ApprovalLine> var1 = approvalLineService.getApprovalLineByApprovalNo(approval.getApprovalNo());
					List<ApprovalLineDto> tempDtoList = new ArrayList<>();
				    for(int i = 0; i < var1.size(); i++) {
				    	tempDtoList.add(new ApprovalLineDto().toDto(var1.get(i)));
				    }
				    var1Map.put(approval.getApprovalNo(), tempDtoList);
					
				    // 결재 리스트옹 - 내가 해당 결재의 합의자인지 확인
					List<ApprovalLine> temp = approval.getApprovalLines();
					for(int i = 0; i < temp.size(); i++) {
						Boolean bool = false;
						if (temp.get(i).getApprovalLineStep() == 0 && temp.get(i).getEmployee().getEmployeeNo() == employeeNo) {
							bool = true;
						}
						if(bool && !approval.getApprovalStatus().equals("F")) {
							temp2 = approvalService.selectApprovalByApprovalNo(temp.get(i).getApproval().getApprovalNo());
							resultApprovalList.add(temp2);
						}
						
				    }
				}
			}
		}
		// 결재 리스트옹 - 내가 합의자인 approval들을 Dto로 변환
		for (Approval t : resultApprovalList) {
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(t);
			resultApprovalListDto.add(dto);
		}
		model.addAttribute("resultList", resultApprovalListDto);
		model.addAttribute("approvalLineMap", var1Map);
		
		return "/approval/agreementApprovals";
	}
	@GetMapping("/approval/referencedApprovals")
	public String selectReferencedApprovalsList(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		// 내가 결재 라인에 있는 approval 리스트 조회
		List<ApprovalLine> var1Entity = approvalLineService.getApprovalLineByEmployeeNo(employeeNo);
		Map<Long, List<ApprovalLineDto>> var1Map = new HashMap<Long, List<ApprovalLineDto>>();
		Approval temp2 = null;
		List<Approval> resultApprovalList = new ArrayList<Approval>();
		List<ApprovalDto> resultApprovalListDto = new ArrayList<ApprovalDto>();
		
		for (ApprovalLine a : var1Entity) {
			
			
			
			// 결재라인용 - 내가 결재 라인에 있는 approval들의 결재번호로 approval 리스트 조회
			List<Approval> approvalList = approvalService.getApprovalListByApprovalNo(a.getApproval().getApprovalNo(), "Y");
			if (approvalList != null) {
				// 결재라인용 - 결재라인에 있는 결재번호로 approval 리스트 entity -> Dto 변환
				for (Approval approval : approvalList) {
					List<ApprovalLine> var1 = approvalLineService.getApprovalLineByApprovalNo(approval.getApprovalNo());
					List<ApprovalLineDto> tempDtoList = new ArrayList<>();
				    for(int i = 0; i < var1.size(); i++) {
				    	tempDtoList.add(new ApprovalLineDto().toDto(var1.get(i)));
				    }
				    var1Map.put(approval.getApprovalNo(), tempDtoList);
					
				    // 결재 리스트옹 - 내가 해당 결재의 참조자인지 확인
					List<ApprovalLine> temp = approval.getApprovalLines();
					for(int i = 0; i < temp.size(); i++) {
						Boolean bool = false;
						if (temp.get(i).getApprovalLineStep() == -1 && temp.get(i).getEmployee().getEmployeeNo() == employeeNo) {
							bool = true;
						}
						if(bool && !approval.getApprovalStatus().equals("F")) {
							temp2 = approvalService.selectApprovalByApprovalNo(temp.get(i).getApproval().getApprovalNo());
							resultApprovalList.add(temp2);
						}
						
				    }
				}
			}
		}
		// 결재 리스트옹 - 내가 참조자인 approval들을 Dto로 변환
		for (Approval t : resultApprovalList) {
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(t);
			resultApprovalListDto.add(dto);
		}
		model.addAttribute("resultList", resultApprovalListDto);
		model.addAttribute("approvalLineMap", var1Map);
		
		return "/approval/referencedApprovals";
	}
	@GetMapping("/approval/withdrawnApprovals")
	public String selectWithdrawnApprovalsList() {
		return "/approval/withdrawnApprovals";
	}
	
	

	

}
