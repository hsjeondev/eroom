package com.eroom.approval.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.approval.dto.ApprovalDto;
import com.eroom.approval.dto.ApprovalFormatDto;
import com.eroom.approval.dto.ApprovalLineDto;
import com.eroom.approval.dto.ApprovalRequestDto;
import com.eroom.approval.entity.Approval;
import com.eroom.approval.entity.ApprovalFormat;
import com.eroom.approval.entity.ApprovalLine;
import com.eroom.approval.entity.ApprovalSignature;
import com.eroom.approval.service.ApprovalFormatService;
import com.eroom.approval.service.ApprovalLineService;
import com.eroom.approval.service.ApprovalService;
import com.eroom.approval.service.ApprovalSignatureService;
import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.service.DriveService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.EmployeeService;
import com.eroom.employee.service.StructureService;
import com.eroom.security.EmployeeDetails;
import com.eroom.websocket.ApprovalWebSocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class ApprovalController {
	
	private final ApprovalService approvalService;
	private final ApprovalLineService approvalLineService;
	private final ApprovalFormatService approvalFormatService;
	private final StructureService structureService;
	private final EmployeeService employeeService;
	private final DriveService driveService;
	private final ApprovalWebSocketHandler approvalWebSocketHandler;
	private final ApprovalSignatureService approvalSignatureService;
	
	
	// 내가 올린 결재 리스트 조회
	// 비동기 방식을 사용해서 해당 RequestParam들 사용하지 않는 상태. 비동기 완료하면 없애자.
	@GetMapping("/approval/myRequestedApprovals")
	public String selectMyRequestedApprovalsList(
			Model model, Authentication authentication
			) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		model.addAttribute("employee", employee);
		ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(employee);
		model.addAttribute("approvalSignature", approvalSignature);
		// 관리자용 전체(visible Y) 결재 보기 - 회의시 필요없는 기능으로 판단. 필요할 수 있으니 주석으로 남겨두기
//		if (employee.getEmployeeName().contains("admin")) {
//			List<Approval> temp = approvalService.findAllApprovalsVisibleY("Y");
//			List<ApprovalDto> resultList = new ArrayList<ApprovalDto>();
//			Map<Long, List<ApprovalLineDto>> approvalLineMap = new HashMap<Long, List<ApprovalLineDto>>();
//			for (Approval approval : temp) {
//				// approval 리스트의 approval_no를 사용해서 approval_line 리스트 조회
//				List<ApprovalLine> temp2 = approvalLineService.getApprovalLineByApprovalNo(approval.getApprovalNo());
//				List<ApprovalLineDto> approvalLineDtoList = new ArrayList<ApprovalLineDto>();
//				for (ApprovalLine approvalLine : temp2) {
//					ApprovalLineDto approvalLineDto = new ApprovalLineDto().toDto(approvalLine);
//					approvalLineDtoList.add(approvalLineDto);
//				}
//				// approval_line 리스트를 approval_no를 키로 하는 맵에 저장
//				approvalLineMap.put(approval.getApprovalNo(), approvalLineDtoList);
//				
//				
//				ApprovalDto dto = new ApprovalDto();
//				dto = dto.toDto(approval);
//				resultList.add(dto);
//			}
//			model.addAttribute("approvalLineMap", approvalLineMap);
//			model.addAttribute("resultList", resultList);
//			
//			
//			return "/approval/myRequestedApprovals";
//			
//		}
		
		// 결재 양식 리스트 조회
		List<ApprovalFormat> approvalFormatEntityList = approvalFormatService.getApprovalFormatList();
		List<ApprovalFormatDto> approvalFormatList = new ArrayList<ApprovalFormatDto>();
		for (ApprovalFormat format : approvalFormatEntityList) {
			ApprovalFormatDto dto = new ApprovalFormatDto();
			dto = dto.toDto(format);
			approvalFormatList.add(dto);
		}
		model.addAttribute("approvalFormatList", approvalFormatList);
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
	// 비동기 방식을 위한 메소드
	@GetMapping("/approval/myRequestedApprovals/fragment")
	public String getMyRequestedApprovalsFragment(
			Model model, Authentication authentication,
			@RequestParam(name = "formatNo",required = false) Long formatNo,
			@RequestParam(name = "dateSort",required = false) String dateSort,
			@RequestParam(name = "approvalStatus",required = false) String approvalStatus
	) {
		
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		model.addAttribute("employee", employee);
		ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(employee);
		model.addAttribute("approvalSignature", approvalSignature);
		// 결재 양식 리스트 조회
				List<ApprovalFormat> approvalFormatEntityList = approvalFormatService.getApprovalFormatList();
				List<ApprovalFormatDto> approvalFormatList = new ArrayList<ApprovalFormatDto>();
				for (ApprovalFormat format : approvalFormatEntityList) {
					ApprovalFormatDto dto = new ApprovalFormatDto();
					dto = dto.toDto(format);
					approvalFormatList.add(dto);
				}
				model.addAttribute("approvalFormatList", approvalFormatList);
				// 내가 올린 approval 리스트 조회
				List<Approval> temp = approvalService.getMyRequestedApprovals(employee.getEmployeeNo(), "Y");
				
				
				List<ApprovalDto> resultList = new ArrayList<ApprovalDto>();
				Map<Long, List<ApprovalLineDto>> approvalLineMap = new HashMap<Long, List<ApprovalLineDto>>();
				for (Approval approval : temp) {
					if(formatNo != 0) {
						if(!approval.getApprovalFormat().getApprovalFormatNo().equals(formatNo)) {
							continue;
						}
					}
					if(!approvalStatus.equals("0")) {
						if(!approval.getApprovalStatus().equals(approvalStatus)) {
							continue;
						}
					}
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
				if(dateSort.equals("ASC")) {
					resultList.sort((a1, a2) -> a1.getApproval_reg_date().compareTo(a2.getApproval_reg_date()));
				} else {
					resultList.sort((a1, a2) -> a2.getApproval_reg_date().compareTo(a1.getApproval_reg_date()));
				}
				model.addAttribute("approvalLineMap", approvalLineMap);
				model.addAttribute("resultList", resultList);
				
				
		return "approval/cardFragment :: cardFragment";
	}
	
	// 재기안 재결재
	@GetMapping({"approval/create", "/approval/{approvalNo}/edit"})
	public String selectApprovalCreate(Model model, Authentication authentication, @PathVariable(value = "approvalNo", required = false) Long approvalNo) {
		// 새로운 결재인지 수정하는 결재(재기안, 재결재 기능)인지 확인
		if(approvalNo != null) {
			// 수정하는 결재인 경우
			model.addAttribute("mode", "edit");
			// 결재 번호로 결재 정보 조회
			Approval approval = approvalService.selectApprovalByApprovalNo(approvalNo);
			// 결재 정보가 없으면 404 에러 페이지로 이동
			if (approval == null) {
				return "redirect:/error/404";
			}
			ApprovalDto approvalDto = new ApprovalDto().toDto(approval);
			model.addAttribute("approval", approvalDto);
			// 결재 라인 정보 조회
			List<ApprovalLine> temp = approvalLineService.getApprovalLineByApprovalNo(approvalNo);
			List<ApprovalLineDto> approvalLineDtoList = new ArrayList<ApprovalLineDto>();
			for (ApprovalLine approvalLine : temp) {
				ApprovalLineDto approvalLineDto = new ApprovalLineDto().toDto(approvalLine);
				approvalLineDtoList.add(approvalLineDto);
			}
			model.addAttribute("approvalLineList", approvalLineDtoList);
//			파일 조회 - 해당 결재글이 드라이브의 param1에 들어있어야함.
			List<DriveDto> driveList = driveService.findApprovalDriveFiles(approvalNo);
			model.addAttribute("driveList", driveList);
		} else {
			// 새로운 결재인 경우
			model.addAttribute("mode", "create");
		}
		
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
		Structure structure = null;
//		Structure employeeStructure = structureService.getBySeparatorCode(employee.getStructure().getSeparatorCode());
		Structure employeeStructure = employee.getStructure();
		
		if (employeeStructure != null) {
			String parentCode = employeeStructure.getParentCode();


			structure = structureService.getBySeparatorCode(employee.getStructure().getSeparatorCode());
			if (parentCode != null) {
				// 팀이 있다는 뜻
				String teamName = structure != null && structure.getCodeName() != null ? structure.getCodeName() : "-";
				model.addAttribute("teamName", teamName);
				Structure parentStructure = structureService.selectStructureCodeNameByParentCodeEqualsSeparatorCode(parentCode);
				String departmentName = parentStructure.getCodeName();
				model.addAttribute("departmentName", departmentName);
			} else {
				// 팀이 없다는 뜻
				String departmentName = structure != null && structure.getCodeName() != null ? structure.getCodeName() : "-";
				model.addAttribute("teamName", "-");
				model.addAttribute("departmentName", departmentName);
			}
		} else {
			// 구조 자체가 null
			model.addAttribute("teamName", "-");
			model.addAttribute("departmentName", "-");
		}
        // 현재 날짜
        model.addAttribute("now", DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDateTime.now()));
        // 부서 코드네임 조회
		List<SeparatorDto> structureList = employeeService.findDistinctStructureNames();
		model.addAttribute("structureList", structureList);
		
	    if (!model.containsAttribute("approval")) {
	        model.addAttribute("approval", new ApprovalDto());
	    }
	    
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
	public Map<String, Object> selectApprovalFormat(@RequestBody ApprovalFormatDto dto) {
		// 결재 양식 조회
		ApprovalFormat approvalFormat = approvalFormatService.getApprovalFormat(dto.getApproval_format_no());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("res_code", "500");
		map.put("res_msg", "양식 조회 실패");
		if(approvalFormat != null) {
			map.put("res_code", "200");
			map.put("res_msg", "양식 조회 성공");
			ApprovalFormatDto approvalFormatDto = new ApprovalFormatDto().toDto(approvalFormat);
			map.put("approvalFormatContent", approvalFormatDto.getApproval_format_content());
			map.put("approvalFormatTitle", approvalFormatDto.getApproval_format_title());
			if(dto.getEdit_approval_no() != null) {
				// 수정하는 결재인 경우
				Approval approval = approvalService.selectApprovalByApprovalNo(dto.getEdit_approval_no());
				ApprovalDto approvalDto = new ApprovalDto().toDto(approval);
				if (approval != null) {
//					map.put("approval", approvalDto);
					map.put("approvalContent", approvalDto.getApproval_content());
					map.put("mode", "edit");
				} else {
					map.put("mode", "create");
				}
			}
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
	
//	기존 방식
//	public Map<String, String> createApproval(@RequestBody ApprovalRequestDto dto, Authentication authentication) {
	//	파일 추가 버전
	// 결재 생성 Create
	@PostMapping("/approval/create")
	@ResponseBody
	public Map<String, String> createApproval(
		    @RequestPart("title") String title,
		    @RequestPart("format_no") String formatNo,
		    @RequestPart(value = "editApprovalNo", required = false) String editApprovalNo,
		    @RequestPart("writer") String writerJson,
		    @RequestPart("content") String contentJson,
		    @RequestPart("approverIds") String approverIdsJson,
		    @RequestPart("approverSteps") String approverStepsJson,
		    @RequestPart("agreerIds") String agreerIdsJson,
		    @RequestPart("agreerSteps") String agreerStepsJson,
		    @RequestPart("refererIds") String refererIdsJson,
		    @RequestPart("refererSteps") String refererStepsJson,
		    @RequestPart(value = "approvalAttachFileIds", required = false) String approvalAttachFileIdsJson,
		    @RequestPart(value = "files", required = false) List<MultipartFile> files,
		    Authentication authentication
		) throws JsonMappingException, JsonProcessingException {
		Map<String, String> map = new HashMap<String, String>();

		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		
//		파일 추가하면서 추가한 부분
		// JSON String을 파싱해서 실제 객체로 변환해야 함
	    ObjectMapper mapper = new ObjectMapper();

	    Map<String, String> content = mapper.readValue(contentJson, new TypeReference<Map<String, String>>() {});
	    List<Long> approverIds = mapper.readValue(approverIdsJson, new TypeReference<List<Long>>() {});
	    List<Integer> approverSteps = mapper.readValue(approverStepsJson, new TypeReference<List<Integer>>() {});
	    List<Long> agreerIds = mapper.readValue(agreerIdsJson, new TypeReference<List<Long>>() {});
	    List<Integer> agreerSteps = mapper.readValue(agreerStepsJson, new TypeReference<List<Integer>>() {});
	    List<Long> refererIds = mapper.readValue(refererIdsJson, new TypeReference<List<Long>>() {});
	    List<Integer> refererSteps = mapper.readValue(refererStepsJson, new TypeReference<List<Integer>>() {});
	    List<Long> approvalAttachFileIds = mapper.readValue(approvalAttachFileIdsJson, new TypeReference<List<Long>>() {});
	    
	    Long parsedEditApprovalNo = (editApprovalNo == null || editApprovalNo.isEmpty()) 
                ? null 
                : Long.valueOf(editApprovalNo);
	    
	    ApprovalRequestDto dto = ApprovalRequestDto.builder()
	    						.title(title)
	    						.format_no(Long.valueOf(formatNo))
	    						.editApprovalNo(parsedEditApprovalNo)
	    						.writer(new EmployeeDto().toDto(employee))
	    						.content(content)
	    						.approverIds(approverIds)
	    						.approverSteps(approverSteps)
	    						.agreerIds(agreerIds)
	    						.agreerSteps(agreerSteps)
	    						.refererIds(refererIds)
	    						.refererSteps(refererSteps)
	    						.approvalAttachFileIds(approvalAttachFileIds)
	    						.files(files)
	    						.build();
//		파일 추가하면서 추가한 부분
		int result = approvalService.createApproval(dto, employeeNo);
		
		
		map.put("res_code", "500");
		map.put("res_msg", "결재 생성 실패");
		if (result > 0) {
			map.put("res_code", "200");
			map.put("res_msg", "결재 생성 성공");
		}
		return map;

	}
	
	@GetMapping("/approval/{approvalNo}/detail")
	public String selectApprovalDetail(@PathVariable("approvalNo") Long approvalNo, Model model, Authentication authentication, HttpServletRequest request) {
//		String uri = request.getRequestURI();
//		if(uri.endsWith("/detail")) {
//			// 상세 페이지 처리 로직
//			System.out.println("상세 페이지 처리 로직");
//		} else if(uri.endsWith("/pdf")) {
//			// pdf 처리 로직
//			System.out.println("pdf 처리 로직");
//		}
		
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
			ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(approvalLineDto.getEmployee());
			if(approvalSignature != null) {
				String encodedBase64 = approvalSignatureService.encodeToBase64(approvalSignature.getApprovalSignatureBlob());
				approvalLineDto.setBase64URL(encodedBase64);
			}
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
		
		String strTemp = String.valueOf(approvalDto.getApproval_no());
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 8 - strTemp.length(); i++) {
			sb.append("0");
		}
		sb.append(strTemp);
		String approvalNoFormatted = "FL007-" + sb.toString();
		model.addAttribute("approvalNoFormatted", approvalNoFormatted);
		
//		파일 조회 - 해당 결재글이 드라이브의 param1에 들어있어야함.
		List<DriveDto> driveList = driveService.findApprovalDriveFiles(approvalNo);
		model.addAttribute("driveList", driveList);
//		파일 조회 - 해당 결재글이 드라이브의 param1에 들어있어야함.
		
		return "/approval/detail";
	}
	
	@DeleteMapping("/approval/{approvalNo}/delete")
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
	public Map<String, String> fallBackApproval(@PathVariable("fallBackNo") Long approvalNo,
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
	

	// 요청 받은 결재(내가 결재라인)
	@GetMapping("/approval/receivedApprovals")
	public String selectReceivedApprovalsList(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(employee);
		model.addAttribute("approvalSignature", approvalSignature);
		// 내가 결재 라인에 있는 approval 리스트 조회
		List<ApprovalLine> var1Entity = approvalLineService.getApprovalLineByEmployeeNo(employeeNo);
		Map<Long, List<ApprovalLineDto>> var1Map = new HashMap<Long, List<ApprovalLineDto>>();
		Approval temp2 = null;
		List<Approval> resultApprovalList = new ArrayList<Approval>();
		List<ApprovalDto> resultApprovalListDto = new ArrayList<ApprovalDto>();
		// 결재 양식 리스트 조회
		List<ApprovalFormat> approvalFormatEntityList = approvalFormatService.getApprovalFormatList();
		List<ApprovalFormatDto> approvalFormatList = new ArrayList<ApprovalFormatDto>();
		for (ApprovalFormat format : approvalFormatEntityList) {
			ApprovalFormatDto dto = new ApprovalFormatDto();
			dto = dto.toDto(format);
			approvalFormatList.add(dto);
		}
		model.addAttribute("approvalFormatList", approvalFormatList);
		
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
					Boolean bool = false;
					Boolean stackBool = false;
					int count = 0;
					// step이 0인 사람(합의자) 중 결재 상태가 A 아닌 사람이 한명이라도 있으면 stackBool = false처리
					for(int i = 0; i < temp.size(); i++) {
						if(temp.get(i).getApprovalLineStep() != 0) {
							continue;
						}
						if(temp.get(i).getApprovalLineStep() == 0) {
							count++;
							if(!temp.get(i).getApprovalLineStatus().equals("A")) {
								stackBool = false;
								break;
							} else if(temp.get(i).getApprovalLineStatus().equals("A")) {
								stackBool = true;
							}
						}
						
						
				    }
					// 결재 라인에 있는 사람들 중~
					for(int i = 0; i < temp.size(); i++) {
						bool = false;
						if(temp.get(i).getApprovalLineStep() >= 1) {
							// 나보다 앞에서 결재 하는 사람의 status확인
							if (i > 0) {
								if(temp.get(i - 1).getApprovalLineStatus().equals("A") && temp.get(i).getEmployee().getEmployeeNo() == employeeNo) {
									bool = true;
								}
							// 내가 제일 처음 결재자인 경우
							} else if (i == 0) {
								if(temp.get(i).getEmployee().getEmployeeNo() == employeeNo) {
									bool = true;
								}
							}
						}
						// 모든 합의자가 status = A, 나의 결재 차례 이후인 경우에만 approval을 담아서 보내기
						if(count == 0) {
							if(bool && !approval.getApprovalStatus().equals("F")) {
								temp2 = approvalService.selectApprovalByApprovalNo(temp.get(i).getApproval().getApprovalNo());
								resultApprovalList.add(temp2);
							}
						} else {
							if(bool && stackBool && !approval.getApprovalStatus().equals("F")) {
								temp2 = approvalService.selectApprovalByApprovalNo(temp.get(i).getApproval().getApprovalNo());
								resultApprovalList.add(temp2);
							}
						}
					}
				}
			}
		}
		// 결재 리스트옹 - 내가 결재 순서인 approval들을 Dto로 변환
//		List<Approval> orderByDESCRegDate = new ArrayList<Approval>();
//		orderByDESCRegDate = resultApprovalList.sort(null)
		resultApprovalList.sort((a1, a2) -> a2.getApprovalRegDate().compareTo(a1.getApprovalRegDate()));
		for (Approval t : resultApprovalList) {
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(t);
			resultApprovalListDto.add(dto);
		}
		model.addAttribute("resultList", resultApprovalListDto);
		model.addAttribute("approvalLineMap", var1Map);
		
		return "/approval/receivedApprovals";
	}
	
	
	
	
	
	// 비동기 방식을 위한 메소드
	@GetMapping("/approval/receivedApprovals/fragment")
	public String getReceivedApprovalsApprovalsApprovalsFragment(
			Model model, Authentication authentication,
			@RequestParam(name = "formatNo",required = false) Long formatNo,
			@RequestParam(name = "dateSort",required = false) String dateSort,
			@RequestParam(name = "approvalStatus",required = false) String approvalStatus
	) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(employee);
		model.addAttribute("approvalSignature", approvalSignature);
		// 내가 결재 라인에 있는 approval 리스트 조회
		List<ApprovalLine> var1Entity = approvalLineService.getApprovalLineByEmployeeNo(employeeNo);
		Map<Long, List<ApprovalLineDto>> var1Map = new HashMap<Long, List<ApprovalLineDto>>();
		Approval temp2 = null;
		List<Approval> resultApprovalList = new ArrayList<Approval>();
		List<ApprovalDto> resultApprovalListDto = new ArrayList<ApprovalDto>();
		// 결재 양식 리스트 조회
		List<ApprovalFormat> approvalFormatEntityList = approvalFormatService.getApprovalFormatList();
		List<ApprovalFormatDto> approvalFormatList = new ArrayList<ApprovalFormatDto>();
		for (ApprovalFormat format : approvalFormatEntityList) {
			ApprovalFormatDto dto = new ApprovalFormatDto();
			dto = dto.toDto(format);
			approvalFormatList.add(dto);
		}
		model.addAttribute("approvalFormatList", approvalFormatList);
		
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
					Boolean bool = false;
					Boolean stackBool = false;
					int count = 0;
					// step이 0인 사람(합의자) 중 결재 상태가 A 아닌 사람이 한명이라도 있으면 stackBool = false처리
					for(int i = 0; i < temp.size(); i++) {
						if(temp.get(i).getApprovalLineStep() != 0) {
							continue;
						}
						if(temp.get(i).getApprovalLineStep() == 0) {
							count++;
							if(!temp.get(i).getApprovalLineStatus().equals("A")) {
								stackBool = false;
								break;
							} else if(temp.get(i).getApprovalLineStatus().equals("A")) {
								stackBool = true;
							}
						}
						
						
				    }
					// 결재 라인에 있는 사람들 중~
					for(int i = 0; i < temp.size(); i++) {
						bool = false;
						if(temp.get(i).getApprovalLineStep() >= 1) {
							// 나보다 앞에서 결재 하는 사람의 status확인
							if (i > 0) {
								if(temp.get(i - 1).getApprovalLineStatus().equals("A") && temp.get(i).getEmployee().getEmployeeNo() == employeeNo) {
									bool = true;
								}
							// 내가 제일 처음 결재자인 경우
							} else if (i == 0) {
								if(temp.get(i).getEmployee().getEmployeeNo() == employeeNo) {
									bool = true;
								}
							}
						}
						// 모든 합의자가 status = A, 나의 결재 차례 이후인 경우에만 approval을 담아서 보내기
						if(count == 0) {
							if(bool && !approval.getApprovalStatus().equals("F")) {
								temp2 = approvalService.selectApprovalByApprovalNo(temp.get(i).getApproval().getApprovalNo());
								resultApprovalList.add(temp2);
							}
						} else {
							if(bool && stackBool && !approval.getApprovalStatus().equals("F")) {
								temp2 = approvalService.selectApprovalByApprovalNo(temp.get(i).getApproval().getApprovalNo());
								resultApprovalList.add(temp2);
							}
						}
					}
				}
			}
		}
		// 결재 리스트옹 - 내가 결재 순서인 approval들을 Dto로 변환
//		List<Approval> orderByDESCRegDate = new ArrayList<Approval>();
//		orderByDESCRegDate = resultApprovalList.sort(null)
		for (Approval t : resultApprovalList) {
			if(formatNo != 0) {
				if(!t.getApprovalFormat().getApprovalFormatNo().equals(formatNo)) {
					continue;
				}
			}
			if(!approvalStatus.equals("0")) {
				if(!t.getApprovalStatus().equals(approvalStatus)) {
					continue;
				}
			}
			
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(t);
			resultApprovalListDto.add(dto);
		}
		if(dateSort.equals("ASC")) {
			resultApprovalListDto.sort((a1, a2) -> a1.getApproval_reg_date().compareTo(a2.getApproval_reg_date()));
		} else {
			resultApprovalListDto.sort((a1, a2) -> a2.getApproval_reg_date().compareTo(a1.getApproval_reg_date()));
		}
		model.addAttribute("resultList", resultApprovalListDto);
		model.addAttribute("approvalLineMap", var1Map);

		return "approval/cardFragment :: cardFragment";
	}

	// 합의 결재
	@GetMapping("/approval/agreementApprovals")
	public String selectAgreementApprovalsList(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(employee);
		model.addAttribute("approvalSignature", approvalSignature);
		// 내가 결재 라인에 있는 approval 리스트 조회
		List<ApprovalLine> var1Entity = approvalLineService.getApprovalLineByEmployeeNo(employeeNo);
		Map<Long, List<ApprovalLineDto>> var1Map = new HashMap<Long, List<ApprovalLineDto>>();
		Approval temp2 = null;
		List<Approval> resultApprovalList = new ArrayList<Approval>();
		List<ApprovalDto> resultApprovalListDto = new ArrayList<ApprovalDto>();
		// 결재 양식 리스트 조회
		List<ApprovalFormat> approvalFormatEntityList = approvalFormatService.getApprovalFormatList();
		List<ApprovalFormatDto> approvalFormatList = new ArrayList<ApprovalFormatDto>();
		for (ApprovalFormat format : approvalFormatEntityList) {
			ApprovalFormatDto dto = new ApprovalFormatDto();
			dto = dto.toDto(format);
			approvalFormatList.add(dto);
		}
		model.addAttribute("approvalFormatList", approvalFormatList);
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
		resultApprovalList.sort((a1, a2) -> a2.getApprovalRegDate().compareTo(a1.getApprovalRegDate()));
		for (Approval t : resultApprovalList) {
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(t);
			resultApprovalListDto.add(dto);
		}
		model.addAttribute("resultList", resultApprovalListDto);
		model.addAttribute("approvalLineMap", var1Map);
		
		return "/approval/agreementApprovals";
	}
	// 비동기 방식을 위한 메소드
	@GetMapping("/approval/agreementApprovals/fragment")
	public String getMyAgreementApprovalsApprovalsFragment(
			Model model, Authentication authentication,
			@RequestParam(name = "formatNo",required = false) Long formatNo,
			@RequestParam(name = "dateSort",required = false) String dateSort,
			@RequestParam(name = "approvalStatus",required = false) String approvalStatus
	) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(employee);
		model.addAttribute("approvalSignature", approvalSignature);
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
		resultApprovalList.sort((a1, a2) -> a2.getApprovalRegDate().compareTo(a1.getApprovalRegDate()));
		for (Approval t : resultApprovalList) {
			if(formatNo != 0) {
				if(!t.getApprovalFormat().getApprovalFormatNo().equals(formatNo)) {
					continue;
				}
			}
			if(!approvalStatus.equals("0")) {
				if(!t.getApprovalStatus().equals(approvalStatus)) {
					continue;
				}
			}
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(t);
			resultApprovalListDto.add(dto);
		}
		
		if(dateSort.equals("ASC")) {
			resultApprovalListDto.sort((a1, a2) -> a1.getApproval_reg_date().compareTo(a2.getApproval_reg_date()));
		} else {
			resultApprovalListDto.sort((a1, a2) -> a2.getApproval_reg_date().compareTo(a1.getApproval_reg_date()));
		}
		model.addAttribute("resultList", resultApprovalListDto);
		model.addAttribute("approvalLineMap", var1Map);
		
				
				
		return "approval/cardFragment :: cardFragment";
	}
	
	
	
	
	
	
	// 참조 결재
	@GetMapping("/approval/referencedApprovals")
	public String selectReferencedApprovalsList(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		Long employeeNo = employee.getEmployeeNo();
		model.addAttribute("employee", employee);
		ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(employee);
		model.addAttribute("approvalSignature", approvalSignature);
		// 내가 결재 라인에 있는 approval 리스트 조회
		List<ApprovalLine> var1Entity = approvalLineService.getApprovalLineByEmployeeNo(employeeNo);
		Map<Long, List<ApprovalLineDto>> var1Map = new HashMap<Long, List<ApprovalLineDto>>();
		Approval temp2 = null;
		List<Approval> resultApprovalList = new ArrayList<Approval>();
		List<ApprovalDto> resultApprovalListDto = new ArrayList<ApprovalDto>();
		// 결재 양식 리스트 조회
		List<ApprovalFormat> approvalFormatEntityList = approvalFormatService.getApprovalFormatList();
		List<ApprovalFormatDto> approvalFormatList = new ArrayList<ApprovalFormatDto>();
		for (ApprovalFormat format : approvalFormatEntityList) {
			ApprovalFormatDto dto = new ApprovalFormatDto();
			dto = dto.toDto(format);
			approvalFormatList.add(dto);
		}
		model.addAttribute("approvalFormatList", approvalFormatList);
		
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
		resultApprovalList.sort((a1, a2) -> a2.getApprovalRegDate().compareTo(a1.getApprovalRegDate()));
		for (Approval t : resultApprovalList) {
			ApprovalDto dto = new ApprovalDto();
			dto = dto.toDto(t);
			resultApprovalListDto.add(dto);
		}
		model.addAttribute("resultList", resultApprovalListDto);
		model.addAttribute("approvalLineMap", var1Map);
		
		return "/approval/referencedApprovals";
	}
	
	
	// 비동기 방식을 위한 메소드
	@GetMapping("/approval/referencedApprovals/fragment")
	public String getMyreferencedApprovalsApprovalsFragment(
			Model model, Authentication authentication,
			@RequestParam(name = "formatNo",required = false) Long formatNo,
			@RequestParam(name = "dateSort",required = false) String dateSort,
			@RequestParam(name = "approvalStatus",required = false) String approvalStatus
	) {
		// 로그인한 사용자 정보 가져오기
				EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
				Employee employee = employeeDetails.getEmployee();
				Long employeeNo = employee.getEmployeeNo();
				model.addAttribute("employee", employee);
				ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(employee);
				model.addAttribute("approvalSignature", approvalSignature);
				// 내가 결재 라인에 있는 approval 리스트 조회
				List<ApprovalLine> var1Entity = approvalLineService.getApprovalLineByEmployeeNo(employeeNo);
				Map<Long, List<ApprovalLineDto>> var1Map = new HashMap<Long, List<ApprovalLineDto>>();
				Approval temp2 = null;
				List<Approval> resultApprovalList = new ArrayList<Approval>();
				List<ApprovalDto> resultApprovalListDto = new ArrayList<ApprovalDto>();
				// 결재 양식 리스트 조회
				List<ApprovalFormat> approvalFormatEntityList = approvalFormatService.getApprovalFormatList();
				List<ApprovalFormatDto> approvalFormatList = new ArrayList<ApprovalFormatDto>();
				for (ApprovalFormat format : approvalFormatEntityList) {
					ApprovalFormatDto dto = new ApprovalFormatDto();
					dto = dto.toDto(format);
					approvalFormatList.add(dto);
				}
				model.addAttribute("approvalFormatList", approvalFormatList);
				
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
					if(formatNo != 0) {
						if(!t.getApprovalFormat().getApprovalFormatNo().equals(formatNo)) {
							continue;
						}
					}
					if(!approvalStatus.equals("0")) {
						if(!t.getApprovalStatus().equals(approvalStatus)) {
							continue;
						}
					}
					ApprovalDto dto = new ApprovalDto();
					dto = dto.toDto(t);
					resultApprovalListDto.add(dto);
				}
				if(dateSort.equals("ASC")) {
					resultApprovalListDto.sort((a1, a2) -> a1.getApproval_reg_date().compareTo(a2.getApproval_reg_date()));
				} else {
					resultApprovalListDto.sort((a1, a2) -> a2.getApproval_reg_date().compareTo(a1.getApproval_reg_date()));
				}
				model.addAttribute("resultList", resultApprovalListDto);
				model.addAttribute("approvalLineMap", var1Map);
		
				
				
		return "approval/cardFragment :: cardFragment";
	}
	// 회수 결재
	@GetMapping("/approval/fallBackApprovals")
	public String selectWithdrawnApprovalsList(Model model, Authentication authentication) {
		// 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
		Employee employee = employeeDetails.getEmployee();
		model.addAttribute("employee", employee);
		ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(employee);
		model.addAttribute("approvalSignature", approvalSignature);
		// 결재 양식 리스트 조회
		List<ApprovalFormat> approvalFormatEntityList = approvalFormatService.getApprovalFormatList();
		List<ApprovalFormatDto> approvalFormatList = new ArrayList<ApprovalFormatDto>();
		for (ApprovalFormat format : approvalFormatEntityList) {
			ApprovalFormatDto dto = new ApprovalFormatDto();
			dto = dto.toDto(format);
			approvalFormatList.add(dto);
		}
		model.addAttribute("approvalFormatList", approvalFormatList);
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
			
			if (approval.getApprovalStatus().equals("F")) {
				ApprovalDto dto = new ApprovalDto();
				dto = dto.toDto(approval);
				resultList.add(dto);
			}
		}
		model.addAttribute("approvalLineMap", approvalLineMap);
		model.addAttribute("resultList", resultList);
		
		
		return "/approval/fallBackApprovals";
	}
	// 비동기 방식을 위한 메소드
	@GetMapping("/approval/fallBackApprovals/fragment")
	public String getFallBackApprovalsApprovalsFragment(
			Model model, Authentication authentication,
			@RequestParam(name = "formatNo",required = false) Long formatNo,
			@RequestParam(name = "dateSort",required = false) String dateSort
	) {
			// 로그인한 사용자 정보 가져오기
			EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
			Employee employee = employeeDetails.getEmployee();
			model.addAttribute("employee", employee);
			ApprovalSignature approvalSignature = approvalSignatureService.findMySignature(employee);
			model.addAttribute("approvalSignature", approvalSignature);
			// 결재 양식 리스트 조회
			List<ApprovalFormat> approvalFormatEntityList = approvalFormatService.getApprovalFormatList();
			List<ApprovalFormatDto> approvalFormatList = new ArrayList<ApprovalFormatDto>();
			for (ApprovalFormat format : approvalFormatEntityList) {
				ApprovalFormatDto dto = new ApprovalFormatDto();
				dto = dto.toDto(format);
				approvalFormatList.add(dto);
			}
			model.addAttribute("approvalFormatList", approvalFormatList);
			
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
				
				if (approval.getApprovalStatus().equals("F")) {
					if(formatNo != 0) {
						if(!approval.getApprovalFormat().getApprovalFormatNo().equals(formatNo)) {
							continue;
						}
					}
					ApprovalDto dto = new ApprovalDto();
					dto = dto.toDto(approval);
					resultList.add(dto);
				}
			}
		

			if(dateSort.equals("ASC")) {
				resultList.sort((a1, a2) -> a1.getApproval_reg_date().compareTo(a2.getApproval_reg_date()));
			} else {
				resultList.sort((a1, a2) -> a2.getApproval_reg_date().compareTo(a1.getApproval_reg_date()));
			}
			model.addAttribute("approvalLineMap", approvalLineMap);
			model.addAttribute("resultList", resultList);
				
		return "approval/cardFragment :: cardFragment";
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
//	 리팩토링용
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
	@GetMapping("/pdf/test/test")
	public String testPdftest(Model model, Authentication authentication) {
		Long approvalNo = 42L;
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
				
				String strTemp = String.valueOf(approvalDto.getApproval_no());
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < 8 - strTemp.length(); i++) {
					sb.append("0");
				}
				sb.append(strTemp);
				String approvalNoFormatted = "FL007-" + sb.toString();
				model.addAttribute("approvalNoFormatted", approvalNoFormatted);
				List<DriveDto> driveList = driveService.findApprovalDriveFiles(approvalNo);
				model.addAttribute("driveList", driveList);
		return "approval/templatePdf/detailPdfTemplate";
	}

}
