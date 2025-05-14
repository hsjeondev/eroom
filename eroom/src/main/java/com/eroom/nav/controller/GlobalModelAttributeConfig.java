package com.eroom.nav.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.eroom.nav.dto.NavMenuItemDto;
import com.eroom.nav.service.NavService;
import com.eroom.security.EmployeeDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class GlobalModelAttributeConfig {

    private final NavService navService;

    @ModelAttribute
    public void addNavAttributes(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증된 사용자가 있을 경우
        if (authentication != null && authentication.getPrincipal() instanceof EmployeeDetails) {
            EmployeeDetails user = (EmployeeDetails) authentication.getPrincipal();

            // user를 서비스로 전달
            List<NavMenuItemDto> nav = navService.getHierarchicalNav(user);
            model.addAttribute("navItems", nav);
        } else {
            // 인증되지 않은 경우 빈 nav 항목 전달
            model.addAttribute("navItems", new ArrayList<>());
        }

        // 현재 요청 URI를 가져옵니다.
        String url = request.getRequestURI();
        model.addAttribute("requestURI", url);
    }


}
