package com.eroom.mail.contorller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.mail.dto.MailDto;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.service.MailService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MailController {

	private final MailService service;
	@GetMapping("/mail")
	public String selectMailAll(Model model) {
		
		List<Mail> resultList = service.selectMailAll();
		model.addAttribute("resultList",resultList);
		return "mail/list";
	}
	
	@GetMapping("/mail/detail")
	public String selectMailOne() {
		return "mail/detail";
	}
	
	@GetMapping("/mail/create")
	public String createMailView() {
		return "mail/create";
	}
	
	@PostMapping("/mail/create")
	@ResponseBody
	public Map<String, String> createMailApi(MailDto mailDto) {
		Map<String, String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "메일 등록중 오류가 발생하였습니다.");
		int result = service.createMail(mailDto);
		if(result>0) {
		resultMap.put("res_code", "200");
		resultMap.put("res_msg", "메일이 발송되었습니다.");	
		}
		System.out.println(mailDto);
		System.out.println("asd");
		return resultMap;
	}
}
