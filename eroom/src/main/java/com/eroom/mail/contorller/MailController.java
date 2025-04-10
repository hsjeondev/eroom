package com.eroom.mail.contorller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.mail.dto.MailDto;

@Controller
public class MailController {

	@GetMapping("/mail")
	public String selectMailAll() {
		
		
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
		resultMap.put("res_msg", "게시글 등록중 오류가 발생하였습니다.");
		System.out.println(mailDto);
		System.out.println("asd");
		return resultMap;
	}
}
