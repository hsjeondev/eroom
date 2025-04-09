package com.eroom.mail.contorller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
	public String createApi() {
		return "mail/create";
	}
}
