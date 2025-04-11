package com.eroom.home.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

//모든 컨트롤러에서 현재 요청 URI(requestURI)를 사용할 수 있도록 Model에 자동 등록하는 Advice 클래스
@ControllerAdvice
public class GlobalModelAdvice {

	 @ModelAttribute("requestURI")
	    public String requestURI(HttpServletRequest request) {
	        return request.getRequestURI();
	    }
	
}
