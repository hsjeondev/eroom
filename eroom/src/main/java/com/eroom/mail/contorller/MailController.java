package com.eroom.mail.contorller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.drive.entity.Drive;
import com.eroom.drive.service.DriveService;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.EmployeeService;
import com.eroom.employee.service.StructureService;
import com.eroom.mail.dto.MailDto;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.entity.MailDraft;
import com.eroom.mail.entity.MailReceiver;
import com.eroom.mail.entity.MailStatus;
import com.eroom.mail.service.MailService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MailController {

	private final MailService mailService;
	private final EmployeeService employeeService;
	private final DriveService driveService;
	
	private final StructureService structureService;
	@Value("${ffupload.location}")
	 private String fileDir;
	
	//test
	@GetMapping("/mail/test")
	public String test(Model model) {
		 Map<String, List<EmployeeDto>> teamEmployeeMap = new HashMap<>();
		 List<Employee> empEntityList = employeeService.findAllEmployee();
		 // 팀 조회
		 
		
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
			for(Structure team : teamList) {
				 List<EmployeeDto> employeeList = new ArrayList<EmployeeDto>();
				for(Employee empEntity : empEntityList) {
					if(empEntity.getStructure() != null 
						    && empEntity.getStructure().getSeparatorCode().equals(team.getSeparatorCode())) {
						employeeList.add(new EmployeeDto().toDto(empEntity));
					}
				}
				teamEmployeeMap.put(team.getSeparatorCode(), employeeList);
			}
		}
		// 부서&&팀 없는 사람
		List<EmployeeDto> noTeamList = new ArrayList<>();
		for (Employee empEntity : empEntityList) {
			if (empEntity.getStructure() == null) {
				EmployeeDto dto = new EmployeeDto().toDto(empEntity);
				noTeamList.add(dto);
			}
		}
		teamEmployeeMap.put("noTeam", noTeamList);
		
		// 부서는 있지만 팀이 없는 사람
		for (Structure dept : departmentList) {
		    List<EmployeeDto> notAssignedList = new ArrayList<>();
		    for (Employee empEntity : empEntityList) {
		        Structure empStruct = empEntity.getStructure();

		        if (empStruct != null) {
		            // ① 구조 자체가 부서에 해당 (즉, 구조 자체가 이 부서와 동일)
		            if (empStruct.getSeparatorCode().equals(dept.getSeparatorCode())) {
		                notAssignedList.add(new EmployeeDto().toDto(empEntity));
		            }

		            // ② 혹은 이 부서의 자식인데 팀 소속이 아님
		            else if (empStruct.getParentCode() != null &&
		                     empStruct.getParentCode().equals(dept.getSeparatorCode())) {
		                List<Structure> teamList = teamMap.get(dept.getCodeName());
		                boolean isTeam = teamList.stream()
		                    .anyMatch(t -> t.getSeparatorCode().equals(empStruct.getSeparatorCode()));
		                if (!isTeam) {
		                    notAssignedList.add(new EmployeeDto().toDto(empEntity));
		                }
		            }
		        }
		    }

		    if (!notAssignedList.isEmpty()) {
		        teamEmployeeMap.put(dept.getSeparatorCode() + "_notAssigned", notAssignedList);
		    }
		}

		
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("teamMap", teamMap);
		model.addAttribute("teamEmployeeMap", teamEmployeeMap);
		
		
		return "mail/test";
	}
	
	// 트리구조 입혀보기
	@GetMapping("/mail/mailReceiverCc")
	public String treeTest(Model model,
		
		@AuthenticationPrincipal EmployeeDetails employeeDetails) {
			Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
		    int unreadCount = mailService.countUnreadMails(employeeNo);
		    int totalCount = mailService.countAllReceivedMails(employeeNo);

		    model.addAttribute("unreadMailCount", unreadCount);
		    model.addAttribute("totalMailCount", totalCount);
		 Map<String, List<EmployeeDto>> teamEmployeeMap = new HashMap<>();
		 List<Employee> empEntityList = employeeService.findAllEmployee();
		 // 팀 조회
		 
		
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
			for(Structure team : teamList) {
				 List<EmployeeDto> employeeList = new ArrayList<EmployeeDto>();
				for(Employee empEntity : empEntityList) {
					if(empEntity.getStructure() != null 
						    && empEntity.getStructure().getSeparatorCode().equals(team.getSeparatorCode())) {
						employeeList.add(new EmployeeDto().toDto(empEntity));
					}
				}
				teamEmployeeMap.put(team.getSeparatorCode(), employeeList);
			}
		}
		// 부서&&팀 없는 사람
		List<EmployeeDto> noTeamList = new ArrayList<>();
		for (Employee empEntity : empEntityList) {
			if (empEntity.getStructure() == null) {
				EmployeeDto dto = new EmployeeDto().toDto(empEntity);
				noTeamList.add(dto);
			}
		}
		teamEmployeeMap.put("noTeam", noTeamList);
		
		// 부서는 있지만 팀이 없는 사람
		for (Structure dept : departmentList) {
		    List<EmployeeDto> notAssignedList = new ArrayList<>();
		    for (Employee empEntity : empEntityList) {
		        Structure empStruct = empEntity.getStructure();

		        if (empStruct != null) {
		            // ① 구조 자체가 부서에 해당 (즉, 구조 자체가 이 부서와 동일)
		            if (empStruct.getSeparatorCode().equals(dept.getSeparatorCode())) {
		                notAssignedList.add(new EmployeeDto().toDto(empEntity));
		            }

		            // ② 혹은 이 부서의 자식인데 팀 소속이 아님
		            else if (empStruct.getParentCode() != null &&
		                     empStruct.getParentCode().equals(dept.getSeparatorCode())) {
		                List<Structure> teamList = teamMap.get(dept.getCodeName());
		                boolean isTeam = teamList.stream()
		                    .anyMatch(t -> t.getSeparatorCode().equals(empStruct.getSeparatorCode()));
		                if (!isTeam) {
		                    notAssignedList.add(new EmployeeDto().toDto(empEntity));
		                }
		            }
		        }
		    }

		    if (!notAssignedList.isEmpty()) {
		        teamEmployeeMap.put(dept.getSeparatorCode() + "_notAssigned", notAssignedList);
		    }
		}

		
		model.addAttribute("departmentList", departmentList);
		model.addAttribute("teamMap", teamMap);
		model.addAttribute("teamEmployeeMap", teamEmployeeMap);
		
		return "mail/mailReceiverCc";
	}
	
	// home test
	/*
	@GetMapping("/mail/mailReceiverCc")
	public String homeMailView(Model model,
			@AuthenticationPrincipal EmployeeDetails employeeDetails) {
		Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
	    int unreadCount = mailService.countUnreadMails(employeeNo);
	    int totalCount = mailService.countAllReceivedMails(employeeNo);

	    model.addAttribute("unreadMailCount", unreadCount);
	    model.addAttribute("totalMailCount", totalCount);
		return "mail/mailReceiverCc";
	}
	*/
	
	
	@GetMapping("/mail/receiverTo/fragment")
	public String selectReceiverToFragment(Model model, 
	                                       @AuthenticationPrincipal EmployeeDetails employeeDetails,
	                                       @RequestParam(name = "sortOrder", defaultValue = "latest") String sortOrder) {
	    Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();

	    try {
	        // 받은 메일과 상태 맵을 가져옵니다.
	        List<MailReceiver> received = mailService.getReceivedMailsByEmployee(employeeNo, sortOrder);
	        Map<Long, MailStatus> mailStatusMap = mailService.getStatusMapForMailRecevier(received);

	        // 받은 메일이 없거나 null인 경우를 체크
	        if (received == null || received.isEmpty()) {
	            System.out.println("받은 메일이 없습니다. 직원 번호: " + employeeNo);
	        }
	        
	        // 모델에 추가
	        model.addAttribute("mailStatusMap", mailStatusMap);
	        model.addAttribute("receivedMails", received);
	    } catch (Exception e) {
	        // 예외 로그 출력
	        e.printStackTrace();
	        model.addAttribute("error", "메일을 가져오는 중 오류가 발생했습니다.");
	        return "error"; // 오류 페이지를 반환
	    }

	    // 뷰 리턴
	    return "mail/mailReceiverTo";
	}
	
	// 받은 메일
	@GetMapping("/mail/receiverTo")
	public String selectReceiverToAll(Model model, 
									@AuthenticationPrincipal EmployeeDetails employeeDetails,
									@RequestParam(name = "sortOrder", defaultValue = "latest") String sortOrder) {
	    Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
	    
	    
	    List<MailReceiver> received = mailService.getReceivedMailsByEmployee(employeeNo, sortOrder); 
	   
	    Map<Long, MailStatus> mailStatusMap = mailService.getStatusMapForMailRecevier(received);

		model.addAttribute("mailStatusMap", mailStatusMap);
	    
	    
	    model.addAttribute("receivedMails", received);
	    return "mail/mailReceiverTo";
	}
	
	
	// 본인 조회 다시 receiver "Me"
	
	// 04/17 본인것만 조회되게 
	

	  @GetMapping("/mail/sent") 
	  public String getSentMails(Model model, 
			  					@AuthenticationPrincipal EmployeeDetails employeeDetails,
			  					@RequestParam(name = "sortOrder", defaultValue = "latest") String sortOrder) { 
	  
	  Long employeeNo =employeeDetails.getEmployee().getEmployeeNo();
	  
	  // 조건 받아와서 여기서 적용하기 ( 최신순, 오래된 순 ) 
	  
	  List<Mail> sentMailList = mailService.findMailsBySender(employeeNo,sortOrder);
	  //List<Mail> sentMailList = mailService.findMailsBySender(myEmployeeNo);
	  model.addAttribute("sentMailList", sentMailList);
	  Map<Long, MailStatus> mailStatusMap = mailService.getStatusMapForMails(sentMailList);

	  model.addAttribute("mailStatusMap", mailStatusMap);
	  return "mail/mailSent"; // 뷰 파일 이름 
	  }
	// 임시 저장 조회
		@GetMapping("/mail/draft")
		public String selectDraftMailAll(Model model,
										@AuthenticationPrincipal EmployeeDetails employeeDetails,
										@RequestParam(name = "sortOrder", defaultValue = "latest") String sortOrder) {
			Long employeeNo =employeeDetails.getEmployee().getEmployeeNo();
			
			List<Mail> draftMailList = mailService.findDraftsByEmployee(employeeNo, sortOrder);
		    model.addAttribute("draftMailList", draftMailList);
		    
		    return "mail/mailDraft";
		}
	  /* 테이블 교체 오류 수정중
		@GetMapping("/mail/draft")
		public String getDraftMails(Model model,
		                            @AuthenticationPrincipal EmployeeDetails employeeDetails,
		                            @RequestParam(name = "sortOrder", defaultValue = "latest") String sortOrder) {

		    Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();

		    // 임시저장 상태("Y"로 저장된) 메일만 가져오도록 서비스 메서드 호출
		    List<Mail> draftMailList = mailService.findOnlyDraftMailsBySender(employeeNo, sortOrder);

		    model.addAttribute("draftMailList", draftMailList);

		    return "mail/mailDraft"; // 뷰 파일 이름
		}
		*/
	
	
	
	// 보낸 메일
	// 지금은 이게 받은걸로 되어 있음
//	@GetMapping("/mail/sent")
//	public String selectSentMailAll(Model model) {
//		
//		
//		
//		
//		// 04/11 지금은 전체 메일이 조회됨
//		List<Mail> sentMailList = mailService.selectMailAll();
//		model.addAttribute("sentMailList",sentMailList);
//		
//		return "mail/mailSent";
//	}
	
	// 참조자 메일 < 이건 나중에 메일 기능을 만든다고 하면 쓸 예정

	
	
	
	
	
	
	// 휴지통에서 삭제하는 로직
	/* 테이블 바뀐 뒤 주석 처리
	@PostMapping("/mail/delete/{id}")
	public String deleteMail(@PathVariable("id") Long id,
	                         @RequestParam("redirectUrl") String redirectUrl,
	                         @AuthenticationPrincipal EmployeeDetails employeeDetails) {
	   Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
	    
	   //System.out.println(id+" : "+redirectUrl+" : "+employeeNo);
	   mailService.deleteReceivedMailById(id, employeeNo);  // 서비스에서 삭제 처리

	    return "redirect:" + redirectUrl;
	}*/
		
	@PostMapping("/mail/delete")
    public ResponseEntity<Void> deleteMail(@RequestBody List<Long> mailNos,
    										@AuthenticationPrincipal EmployeeDetails employeeDetails) {
        // mailNos에 포함된 메일들의 visible_yn을 "N"으로 업데이트
		Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
		for (Long mailNo : mailNos) {
			mailService.updateVisibleYn(employeeNo,mailNo, "N");
	    }
        
        return ResponseEntity.ok().build();
    }	
	// 휴지통으로 옮기는 로직 ( N => Y )
	// 단일
	@PostMapping("/mail/trash/{id}")
	public String moveToTrash(@PathVariable("id") Long id,
							  @RequestParam("redirectUrl") String redirectUrl,
							  @AuthenticationPrincipal EmployeeDetails employeeDetails) {
		Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
		mailService.moveToTrash(employeeNo,id);
		return "redirect:" + redirectUrl;
	}
	// 다중
	@PostMapping("/mail/trash")
    @ResponseBody
    public ResponseEntity<Void> trashBulkMail(@RequestBody List<Long> mailNos,
    										@AuthenticationPrincipal EmployeeDetails employeeDetails) {
		Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
		for (Long mailNo : mailNos) {
	        mailService.moveToTrash(employeeNo,mailNo );  // 하나씩 처리
	    }
        return ResponseEntity.ok().build();  // 성공적으로 처리되면 OK 응답
    }
	// 상태 바꾸기
	// 단일
	@PostMapping("/mail/status/important/{id}")
    public String moveImportant(@PathVariable("id") Long id,
                                                @AuthenticationPrincipal EmployeeDetails employeeDetails, 
												@RequestParam("redirectUrl") String redirectUrl){
		Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
		mailService.moveImportant(id,employeeNo);
        return "redirect:" + redirectUrl;
    }
	// 다중 별표 처리( 중요 표시 )
	@PostMapping("/mail/important")
	@ResponseBody
	public ResponseEntity<Void> markBulkImportant(@RequestBody List<Long> mailNos,
	                                              @AuthenticationPrincipal EmployeeDetails employeeDetails) {
	    Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
	    
	    for (Long mailNo : mailNos) {
	        //mailService.moveImportant(mailNo,employeeNo);  
	        mailService.toggleStarred(mailNo,employeeNo);  
	    }
	    
	    return ResponseEntity.ok().build(); 
	}
	// 중요한 메일 조회할곳
	@GetMapping("/mail/important")
	public String selectImportantMailAll(Model model, 
		    @AuthenticationPrincipal EmployeeDetails employeeDetails,
		    @RequestParam(name = "sortOrder", defaultValue = "latest") String sortOrder) {
		
		Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();

		List<Mail> importantMailList = mailService.getImportantMailsByEmployee(employeeNo, sortOrder);
		Map<Long, MailStatus> mailStatusMap = mailService.getStatusMapForMails(importantMailList);
	    model.addAttribute("importantMailList", importantMailList);
	    model.addAttribute("mailStatusMap", mailStatusMap);
		return "mail/mailImportant";
	}
	
	// 휴지통 조회할곳
	
	@GetMapping("/mail/trash")
	public String selectTrashMailAll(Model model, 
	    @AuthenticationPrincipal EmployeeDetails employeeDetails,
	    @RequestParam(name = "sortOrder", defaultValue = "latest") String sortOrder) {

	    Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();

	    List<Mail> trashMailList = mailService.getTrashMailsByEmployee(employeeNo, sortOrder);

	    model.addAttribute("trashMailList", trashMailList);
	    return "mail/mailTrash";
	    //return "mail/test";
	}
	
	// 디테일 파일 다운로드
	/*@GetMapping("/mail/download/{attachmentId}")
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(@PathVariable("attachmentId") Long attachmentId) {
	    // 파일 경로와 파일명을 가져옵니다.
	    Drive drive = mailService.findAttachmentById(attachmentId);

	    // 파일이 없으면 404 반환
	    if (drive == null) {
	        return ResponseEntity.notFound().build();
	    }

	    // drive.getDrivePath()는 이미 전체 경로를 포함하고 있으므로 그대로 사용
	    Path path = Paths.get(fileDir+drive.getDrivePath());
	    Resource resource = null;
	    try {
	        resource = new UrlResource(path.toUri());
	    } catch (MalformedURLException e) {
	        return ResponseEntity.badRequest().build();
	    }

	    // 파일이 존재하면 다운로드
	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + drive.getDriveOriName() + "\"")
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(resource);
	}*/
	@GetMapping("/mail/file/download/{driveAttachNo}")
	public ResponseEntity<Object> fileDownload(@PathVariable("driveAttachNo") Long id) {
	    try {
	        // DB에서 파일 정보 가져오기
	        Drive drive = mailService.findAttachmentById(id);
	        if (drive == null) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        // 파일 경로 생성
	        Path filePath = Paths.get(fileDir + drive.getDrivePath());
	        
	        // 파일 존재 여부 확인
	        if (!Files.exists(filePath)) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        // 한글 파일명 인코딩 처리
	        String encodedFileName = URLEncoder.encode(drive.getDriveOriName(), "UTF-8")
	                                         .replaceAll("\\+", "%20");
	        
	        // 파일 리소스 생성
	        Resource resource = new InputStreamResource(Files.newInputStream(filePath));
	        
	        // 다운로드 응답
	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(resource);
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 다운로드 중 오류가 발생했습니다.");
	    }
	}
	
	// 디테일 ( 파일 불러오기 성공 ) 테이블 바뀐 뒤 주석 처리
	
	@GetMapping("/mail/detail/{id}")
	public String selectMailOne(@PathVariable("id") Long id,
								Model model,
								 @AuthenticationPrincipal EmployeeDetails employeeDetails) { 
		Long employeeNo = employeeDetails.getEmployee().getEmployeeNo(); // employeeNo값 
		Mail mail = mailService.selectMailOne(employeeNo,id);
		model.addAttribute("mail",mail);
		
		List<Drive> attachments = mailService.findMailAttachments(id);
	    model.addAttribute("attachments", attachments);
		
		
		return "mail/mailDetail";
	}
	// 파일 숨기기
	/*
	 * @DeleteMapping("/delete/{attachNo}")
	 */	
	@PostMapping("/mail/hide/file/{attachNo}")
	@ResponseBody
	public Map<String, String> deleteDriveFile(@PathVariable("attachNo") Long driveAttachNo) {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "삭제 실패");
		System.out.println("Deleting file with attachNo: " + driveAttachNo);
		int result = driveService.deleteDriveFile(driveAttachNo);

		if (result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "삭제 성공");
		}

		return resultMap;
	}
	
	
	
	
	
	

//    @PostMapping("/mail/status/trash")
//    public ResponseEntity<Void> moveTrash(@RequestBody MailDto mailDto,
//                                            @AuthenticationPrincipal EmployeeDetails employeeDetails) {
//    	Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
//    	mailService.moveTrash(mailDto.getMail_no(), employeeNo);
//        return ResponseEntity.ok().build();
//    }
	
	
	// 임시 저장 페이지
	@GetMapping("/mail/mailCreateDraft/{id}")
	public String createMailDraftView(Model model,
									@PathVariable("id") Long id) {
		List<Employee> employeeList = mailService.selectEmployeeAll();
		List<SeparatorDto> departments = employeeService.findDistinctStructureNames(); // 부서/팀 리스트 가져오기
		model.addAttribute("employeeList",employeeList);
		model.addAttribute("departments", departments); // 부서 드롭다운용
		// 가져온 값은 mail_no값 이걸로 mail조회하여 보낸 사람 no값 찾기
		Employee sender = mailService.getSenderInfoByMailNo(id);
		if(id != null) {
			
			 Mail mail = mailService.findMailByMailNo(id);

		        // (2) mail_draft 테이블에서 수신자 리스트 불러오기
		        List<MailDraft> draft = mailService.findReceiversByMailNo(id);

		        // (3) drive 테이블에서 첨부파일 리스트 불러오기
		       // List<Drive> mailFiles = mailService.findFilesByMailNo(id);

		        // (4) 모델에 담기
		        // mail 정보 2개 들어갔음
		        model.addAttribute("mail", mail);
		        model.addAttribute("draft", draft);
		       // model.addAttribute("mailFiles", mailFiles);
		}
		
		model.addAttribute("sender", sender);
		return "mail/mailCreateDraft";
	}
	
	// 답장 페이지
	@GetMapping("/mail/mailCreateReply/{id}")
	public String createMailReplyView(Model model,
										@PathVariable("id") Long id) {
		List<Employee> employeeList = mailService.selectEmployeeAll();
		List<SeparatorDto> departments = employeeService.findDistinctStructureNames(); // 부서/팀 리스트 가져오기
		model.addAttribute("employeeList",employeeList);
		model.addAttribute("departments", departments); // 부서 드롭다운용
		// 가져온 값은 mail_no값 이걸로 mail조회하여 보낸 사람 no값 찾기
		Employee sender = mailService.getSenderInfoByMailNo(id);
		
		
		model.addAttribute("sender", sender);
		return "mail/mailCreateReply";
	}
	
	// 주소록에서 메일쓰기
	@GetMapping("/mail/write/{targetEmpNo}")
	public String writeMailFromAddressBook(Model model,
										@PathVariable("targetEmpNo") Long employeeNo) {
		Employee sender = mailService.getEmployeeByNo(employeeNo);
		model.addAttribute("sender", sender);
		return "mail/mailCreateReply";
	}
	
	
	
	// 메일 작성 페이지
	@GetMapping("/mail/mailCreate")
	public String createMailView(Model model
			,@RequestParam(name = "mailNo", required = false) Long mailNo
			) {
		List<Employee> employeeList = mailService.selectEmployeeAll();
		List<SeparatorDto> departments = employeeService.findDistinctStructureNames(); // 부서/팀 리스트 가져오기
		model.addAttribute("employeeList",employeeList);
		model.addAttribute("departments", departments); // 부서 드롭다운용
		
		if(mailNo != null) {
			
			 Mail mail = mailService.findMailByMailNo(mailNo);

		        // (2) mail_draft 테이블에서 수신자 리스트 불러오기
		        List<MailDraft> draft = mailService.findReceiversByMailNo(mailNo);

		        // (3) drive 테이블에서 첨부파일 리스트 불러오기
		        List<Drive> mailFiles = mailService.findFilesByMailNo(mailNo);

		        // (4) 모델에 담기
		        // mail 정보 2개 들어갔음
		        model.addAttribute("mail", mail);
		        model.addAttribute("draft", draft);
		        model.addAttribute("mailFiles", mailFiles);
		}
		return "mail/mailCreate";
	}
	
	// 임시 저장 눌렀을때 ( 메일 작성 페이지처럼 보이게 폐기 예정
//	@GetMapping("/mail/draft/update")
//	public String updateMailDraft(Model model,
//								@RequestParam("mailNo") Long mailNo) {
//		List<Employee> employeeList = mailService.selectEmployeeAll();
//		List<SeparatorDto> departments = employeeService.findDistinctStructureNames(); // 부서/팀 리스트 가져오기
//		model.addAttribute("employeeList",employeeList);
//		model.addAttribute("departments", departments); // 부서 드롭다운용
//		if(mailNo != null) {
//		
//		 Mail mail = mailService.findMailByMailNo(mailNo);
//
//	        // (2) mail_draft 테이블에서 수신자 리스트 불러오기
//	        List<MailDraft> draft = mailService.findReceiversByMailNo(mailNo);
//
//	        // (3) drive 테이블에서 첨부파일 리스트 불러오기
//	        //List<Drive> attachedFiles = driveService.findFilesByMailNo(mailNo);
//
//	        // (4) 모델에 담기
//	        // mail 정보 2개 들어갔음
//	        model.addAttribute("mail", mail);
//	        model.addAttribute("draft", draft);
//		}
//		
//		return "mail/mailDraftUpdate";
//	}
	
	// 메일 임시저장 
	@PostMapping("/mail/draft") // 임시저장
	@ResponseBody
	public Map<String, String> saveDraft(MailDto mailDto,
										@RequestParam(name="mail_files") List<MultipartFile> mailFiles,
										@RequestParam(name="mail_draft_yn") String mailDraftYn) {
		Map<String, String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "임시저장중 오류가 발생하였습니다.");
		int result = 0;
		
			result = mailService.createMail(mailDto,mailFiles ,mailDraftYn);
		
		if(result >0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "임시저장 되었습니다.");	
		}
	    //mailDto.setMailStatus("Y"); // mail_status 직접 세팅
				//mailService.saveDraft(mailDto);
				return resultMap;
	}
	
	
	// 메일 작성 로직
	// mail DB에 데이터 넣는거만 가능
	@PostMapping("/mail/create")
	@ResponseBody
	public Map<String, String> createMailApi(MailDto mailDto,
											 @RequestParam(name="mail_files") List<MultipartFile> mailFiles,
											 @RequestParam(name="mail_draft_yn") String mailDraftYn
											 ) {
		Map<String, String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "메일 등록중 오류가 발생하였습니다.");
		int result = 0;
		// 받는 사람, 제목, 내용, 파일 정보 보내줌
		
			//result = mailService.createMailUpdate(mailDto, mailFiles, mailDraftYn, mailNo);
			result = mailService.createMail(mailDto, mailFiles, mailDraftYn); 
		
		 if(result>0) {
			 resultMap.put("res_code", "200"); resultMap.put("res_msg", "메일이 발송되었습니다."); 
		 }
		 
		return resultMap;
	}
	
	
	
	@GetMapping("/search-employees")
    @ResponseBody
    public List<EmployeeDto> searchEmployees(@RequestParam("separator_code") String separatorCode) {
        if (separatorCode.startsWith("T")) {
            return employeeService.findEmployeesByStructureName(separatorCode); 
        } else {
            return employeeService.findEmployeesByParentCode(separatorCode);
        }
    }
	
	 /*@GetMapping("/mail/detail/{mailNo}")
	    public String viewMailDetail(@PathVariable Long mailNo, Model model) {
	        // mailNo로 메일 조회
	        Mail draftMail = mailService.getMailById(mailNo);

	        // 메일 정보를 Model에 추가
	        model.addAttribute("draftMail", draftMail);
	        
	        // 메일 작성 페이지로 이동
	        return "mail/mailCreate";
	    }*/
	
}
