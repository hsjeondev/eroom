package com.eroom.mail.contorller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	/* 테스트로 만들어 놓은거
	 * @GetMapping("/mail") public String selectMailAll(Model model) { // 조건 필요함
	 * reveiver에 // to일때는 내가 보낸거 // 조건이 cc면 받은거
	 * 
	 * // 조건 더 필요함 status 'N'일때 임시저장x 즉, 발송된 메일 List<Mail> resultList =
	 * service.selectMailAll(); model.addAttribute("resultList",resultList); return
	 * "mail/list"; }
	 */
	
	// 받은 메일
	@GetMapping("/mail/receiverTo")
	public String selectReceiverToAll(Model model, @AuthenticationPrincipal EmployeeDetails user) {
	    Long empNo = user.getEmployee().getEmployeeNo(); // 로그인된 사용자
	    List<MailReceiver> received = mailService.getReceivedMailsByEmployee(empNo); // 💡 서비스로 위임
	    model.addAttribute("receivedMails", received);
	    return "mail/mailReceiverTo";
	}
	
	// 04/17 본인것만 조회되게 
	
	  @GetMapping("/mail/sent") public String getSentMails(Model
	  model, @AuthenticationPrincipal EmployeeDetails user) { Long myEmployeeNo =
	  user.getEmployee().getEmployeeNo();
	  
	  List<Mail> sentMailList = mailService.findMailsBySender(myEmployeeNo);
	  model.addAttribute("sentMailList", sentMailList);
	  
	  return "mail/mailSent"; // 뷰 파일 이름 
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
	
	// 임시 저장 조회할곳
	@GetMapping("/mail/draft")
	public String selectDraftMailAll() {
		
		return "mail/mailDraft";
	}
	
	
	// 중요한 메일 조회할곳
	@GetMapping("/mail/important")
	public String selectImportantMailAll() {
		
		return "mail/mailImportant";
	}
	
	// 휴지통 조회할곳
	@GetMapping("/mail/trash")
	public String selectTrashMailAll() {
		
		return "mail/mailTrash";
	}
	
	// 디테일
	@GetMapping("/mail/detail")
	public String selectMailOne() { 
		return "mail/mailDetail";
	}
	
	// 메일 작성 페이지
	@GetMapping("/mail/mailCreate")
	public String createMailView(Model model) {
		List<Employee> employeeList = mailService.selectEmployeeAll();
		List<SeparatorDto> departments = employeeService.findDistinctStructureNames(); // 부서/팀 리스트 가져오기
		model.addAttribute("employeeList",employeeList);
		
		model.addAttribute("departments", departments); // 부서 드롭다운용
		return "mail/mailCreate";
	}
	// 메일 작성 로직
	// mail DB에 데이터 넣는거만 가능
	@PostMapping("/mail/create")
	@ResponseBody
	public Map<String, String> createMailApi(MailDto mailDto) {
		Map<String, String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "메일 등록중 오류가 발생하였습니다.");
		int result = mailService.createMail(mailDto);
		if(result>0) {
		resultMap.put("res_code", "200");
		resultMap.put("res_msg", "메일이 발송되었습니다.");	
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
	
}
