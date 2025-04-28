package com.eroom.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {

	// 얘는 RequestCache를 무시함
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// 웹소켓을 위한 정보 추가
	    EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
	    HttpSession session = request.getSession();
	    session.setAttribute("employeeNo", employeeDetails.getEmployee().getEmployeeNo());
	    // 웹소켓을 위한 정보 추가
		response.sendRedirect("/");
	}

}
