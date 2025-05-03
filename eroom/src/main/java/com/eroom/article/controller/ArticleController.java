package com.eroom.article.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.article.dto.ArticleDto;
import com.eroom.article.entity.Article;
import com.eroom.article.service.ArticleService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ArticleController {
	
	private final ArticleService articleService;
	
	// 익명 게시판
	@GetMapping("/article/anonymous")
	public String selectBoardAnonymousAll() {
		return "/article/articleAnonymous";
	}
	
	
	
	
	// 공지 게시판 
	@GetMapping("/article/notice")
	public String selectBoardNotice(Model model) {
		List<Article> noticeList = articleService.getNoticeArticles();
        model.addAttribute("noticeList", noticeList);
		return "/article/articleNotice";
	}
	
	// 게시판 작성 페이지
	@GetMapping("/article/articleCreate")
	public String articleCreateView() {
		
		return "/article/articleCreate";
	}
	
	// 작성 로직
	@PostMapping("/article/create")
	@ResponseBody
	public Map<String, String> createArticleApi(@AuthenticationPrincipal EmployeeDetails employeeDetails,
	                                            ArticleDto articleDto) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "공지 등록 중 오류가 발생했습니다.");

	    Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();

	    int result = articleService.createArticle(articleDto, employeeNo);
	    
	    if (result > 0) {
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "공지 작성 완료");
	    }

	    return resultMap;
	}
}
