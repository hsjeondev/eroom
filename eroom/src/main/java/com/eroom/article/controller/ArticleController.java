package com.eroom.article.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.mail.dto.MailDto;

@Controller
public class ArticleController {

	@GetMapping("/article/notice")
	public String selectBoardNotice() {
		return "/article/articleNotice";
	}
	
	@GetMapping("/article/anonymous")
	public String selectBoardAnonymousAll() {
		return "/article/articleAnonymous";
	}
	
	@GetMapping("/article/articleCreate")
	public String articleCreateView() {
		
		return "/article/articleCreate";
	}
	
	@PostMapping("/article/create")
	@ResponseBody
	public Map<String, String> createArticleApi(MailDto mailDto
											 ) {
		Map<String, String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "메일 등록중 오류가 발생하였습니다.");
		int result = 0;
		
		
		 if(result>0) {
			 resultMap.put("res_code", "200"); 
			 resultMap.put("res_msg", "메일이 발송되었습니다."); 
		 }
		 
		return resultMap;
	}
}
