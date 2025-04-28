package com.eroom.mail.contorller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.drive.entity.Drive;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.service.EmployeeService;
import com.eroom.mail.dto.MailDto;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.entity.MailReceiver;
import com.eroom.mail.service.MailService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MailController {

	private final MailService mailService;
	private final EmployeeService employeeService;
	@Value("${ffupload.location}")
	 private String fileDir;
	/* 테스트로 만들어 놓은거
	 * @GetMapping("/mail") public String selectMailAll(Model model) { // 조건 필요함
	 * reveiver에 // to일때는 내가 보낸거 // 조건이 cc면 받은거
	 * 
	 * // 조건 더 필요함 status 'N'일때 임시저장x 즉, 발송된 메일 List<Mail> resultList =
	 * service.selectMailAll(); model.addAttribute("resultList",resultList); return
	 * "mail/list"; }
	 */
	
	// 받은 메일
	/*
	@GetMapping("/mail/receiverTo")
	public String selectReceiverToAll(Model model, 
									@AuthenticationPrincipal EmployeeDetails employeeDetails,
									@RequestParam(name = "sortOrder", defaultValue = "latest") String sortOrder) {
	    Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
	    
	    
	    List<MailReceiver> received = mailService.getReceivedMailsByEmployee(employeeNo, sortOrder); 
	    
	    
	    model.addAttribute("receivedMails", received);
	    return "mail/mailReceiverTo";
	}
	*/
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
	  
	  return "mail/mailSent"; // 뷰 파일 이름 
	  }
	// 임시 저장 조회할곳
		/*@GetMapping("/mail/draft")
		public String selectDraftMailAll(Model model,
										@A) {
			
			return "mail/mailDraft";
		}*/
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
	@GetMapping("/mail/receiverCc")
	public String selectReceiverCcAll() {
		return "mail/mailReceiverCc";
	}
	
	
	
	
	// 중요한 메일 조회할곳
	@GetMapping("/mail/important")
	public String selectImportantMailAll() {
		
		return "mail/mailImportant";
	}
	
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
	}
	// 휴지통으로 옮기는 로직 ( N => Y )
	@PostMapping("/mail/trash/{id}")
	public String moveToTrash(@PathVariable("id") Long id,
							  @RequestParam("redirectUrl") String redirectUrl,
							  @AuthenticationPrincipal EmployeeDetails employeeDetails) {
		Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
		mailService.moveToTrash(employeeNo,id);
		return "redirect:" + redirectUrl;
	}
	*/
	// 휴지통 조회할곳
	/*
	@GetMapping("/mail/trash")
	public String selectTrashMailAll(Model model, 
	    @AuthenticationPrincipal EmployeeDetails employeeDetails,
	    @RequestParam(name = "sortOrder", defaultValue = "latest") String sortOrder) {

	    Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();

	    List<MailReceiver> trashMailList = mailService.getTrashMailsByEmployee(employeeNo, sortOrder);

	    model.addAttribute("trashMailList", trashMailList);
	    return "mail/mailTrash";
	}
	*/
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
	@GetMapping("/mail/download/{driveAttachNo}")
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
	
	// 디테일 ( 파일 불러오기 성공 )
	/* 테이블 바뀐 뒤 주석 처리
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
	*/
	
	// 메일 작성 페이지
	@GetMapping("/mail/mailCreate")
	public String createMailView(Model model) {
		List<Employee> employeeList = mailService.selectEmployeeAll();
		List<SeparatorDto> departments = employeeService.findDistinctStructureNames(); // 부서/팀 리스트 가져오기
		model.addAttribute("employeeList",employeeList);
		
		model.addAttribute("departments", departments); // 부서 드롭다운용
		return "mail/mailCreate";
	}
	
	// 메일 임시저장 

	@PostMapping("/mail/draft") // 임시저장
	@ResponseBody
	public Map<String, String> saveDraft(MailDto mailDto,
										@RequestParam(name="mail_status") String mailStatus) {
		Map<String, String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "임시저장중 오류가 발생하였습니다.");
		int result = mailService.saveMail(mailDto, mailStatus);
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
											 @RequestParam(name="mail_files") List<MultipartFile> mailFiles) {
		Map<String, String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "메일 등록중 오류가 발생하였습니다.");
		
		// 받는 사람, 제목, 내용, 파일 정보 보내줌
		  int result = mailService.createMail(mailDto, mailFiles); 
		  
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
