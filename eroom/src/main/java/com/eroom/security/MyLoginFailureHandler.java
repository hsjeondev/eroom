package com.eroom.security;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		String errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다.";
		
		if(exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {
			errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
		} else if(exception instanceof DisabledException) {
			errorMessage = "계정이 비활성화 되었습니다.";
		} else if(exception instanceof CredentialsExpiredException) {
			errorMessage = "비밀번호 유효기간이 만료되었습니다.";
		}
		
		errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
//		response.sendRedirect("/login?error=true&errorMsg="+errorMessage);
		response.sendRedirect("/login");
		
	}

}
