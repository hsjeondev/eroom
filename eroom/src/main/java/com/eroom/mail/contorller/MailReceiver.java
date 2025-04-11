package com.eroom.mail.contorller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MailReceiver {

	@GetMapping("/mail/receiver")
	public String MailReceiver() {
		
		return "/mailReceiver";
	}
}
