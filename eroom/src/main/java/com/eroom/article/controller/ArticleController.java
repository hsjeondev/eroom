package com.eroom.article.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.article.dto.ArticleDto;
import com.eroom.article.entity.Article;
import com.eroom.article.service.ArticleService;
import com.eroom.drive.entity.Drive;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ArticleController {
	
	private final ArticleService articleService;
	@Value("${ffupload.location}")
	 private String fileDir;
	
	// index 홈 카드 비동기
	@GetMapping("/article/test-json")
	@ResponseBody
	public List<ArticleDto> getTestNotices() {
	    List<Article> generalNotices = articleService.test2();

	    // 엔티티를 그대로 JSON으로 노출하는 건 권장하지 않으니 DTO로 변환하세요.
	    List<ArticleDto> dtoList = generalNotices.stream()
	        .map(article -> new ArticleDto(article.getArticleTitle(), article.getArticleRegDate(),article.getArticleNo()))
	        .collect(Collectors.toList());
	    
	    return dtoList;
	}
	
	// 홈 카드 테스트
	@GetMapping("/article/test")
	public String test2(Model model){
		List<Article> generalNotices = articleService.test2();
		model.addAttribute("generalNotices", generalNotices);
		
		return "article/test";
	}
	
	
	
	// 익명 게시판
	@GetMapping("/article/anonymous")
	public String selectBoardAnonymousAll() {
		return "article/articleAnonymous";
	}
	
	// 공지 게시판 삭제 버튼 클릭시
	@PostMapping("/article/notice/delete")
	public String deleteArticleNotice(@RequestParam("articleNo") Long articleNo) {
		articleService.deleteArticleNotice(articleNo);
	    return "redirect:/article/notice";
	}
	
	// 게시글 수정중 기존 파일 삭제
	@PostMapping("/article/file/delete/{attachNo}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteAttachedFile(@PathVariable("attachNo") Long attachNo) {
		System.out.println("attachNo received: " + attachNo);
		Map<String, Object> result = new HashMap<>();
	    try {
	    	articleService.markAsDeleted(attachNo);  // visibleYn = "N" 처리 등
	        result.put("res_code", 200);
	        result.put("res_msg", "파일이 삭제되었습니다.");
	    } catch (Exception e) {
	        result.put("res_code", 500);
	        result.put("res_msg", "삭제 실패: " + e.getMessage());
	    }
	    return ResponseEntity.ok(result);
	}
	
	// 게시글 수정
	@PostMapping("/article/update")
	@ResponseBody
	public Map<String, String> updateArticleApi(@AuthenticationPrincipal EmployeeDetails employeeDetails,
	                                            ArticleDto articleDto,
	                                            @RequestParam("article_files") List<MultipartFile> articleFiles) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "공지 수정 중 오류가 발생했습니다.");

	    Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();
	    int result = articleService.updateArticle(articleDto,articleFiles);
	    
	    if (result > 0) {
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "공지글이 수정 완료되었습니다.");
	    }

	    return resultMap;
	}
	
	// 게시글 수정 페이지
	@GetMapping("/article/articleEdit/{id}")
	public String editArticleNoticeForm(@PathVariable("id") Long id, Model model) {
		
	    Article article = articleService.selectArticleNoticeOne(id);
	    
	    List<Drive> attachList = articleService.findArticleAttaNoticechments(id);
	    model.addAttribute("article", article);
	    model.addAttribute("attachList", attachList);
	    return "article/articleEdit"; // Thymeleaf 템플릿 경로
	}
	
	// 게시판 디테일 파일 다운로드
	@GetMapping("/article/notice/file/download/{driveAttachNo}")
	public ResponseEntity<Object> fileDownload(@PathVariable("driveAttachNo") Long id) {
	    try {
	        // DB에서 파일 정보 가져오기
	        Drive drive = articleService.findAttachmentById(id);
	        if (drive == null) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        // 파일 경로 생성
	        Path filePath = Paths.get(fileDir + drive.getDrivePath());
	        
	        // 파일 존재 여부 확인
	        if (!Files.exists(filePath)) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        // 한글 파일명 인코딩 처리
	        String encodedFileName = URLEncoder.encode(drive.getDriveOriName(), "UTF-8")
	                                         .replaceAll("\\+", "%20");
	        
	        // 파일 리소스 생성
	        Resource resource = new InputStreamResource(Files.newInputStream(filePath));
	        
	        // 다운로드 응답
	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(resource);
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 다운로드 중 오류가 발생했습니다.");
	    }
	}
	
	
	// 공지 게시판 디테일
	// 파일 조회
	@GetMapping("/article/notice/detail/{id}")
	public String selectArticleNoticeOne(@PathVariable("id") Long id,
										Authentication autentication, Model model) {
	    
	    Article articleNotice = articleService.selectArticleNoticeOne(id); // 공지사항 하나 조회
	    model.addAttribute("articleNotice", articleNotice);
	    List<Drive> attachArticleNoticeList = articleService.findArticleAttaNoticechments(id);
	    model.addAttribute("attachArticleNoticeList", attachArticleNoticeList);
	    // 로그인한 사용자
		EmployeeDetails employeeDetails = (EmployeeDetails) autentication.getPrincipal();
		String employeeName = employeeDetails.getEmployee().getEmployeeName();
		model.addAttribute("loginUser", employeeName);
	    return "article/articleDetail"; // Thymeleaf 템플릿 경로	
	}
	
	
	@GetMapping("/article/articleDetail")
	public String test() {
		return"article/articleDetail";
	}
	
	// 공지 게시판 
	@GetMapping("/article/notice")
	public String selectBoardNotice(Model model) {
		List<Article> noticeList = articleService.getNoticeArticles();
        model.addAttribute("noticeList", noticeList);
		return "article/articleNotice";
	}
	
	// 게시판 작성 페이지
	@GetMapping("/article/articleCreate")
	public String articleCreateView() {
		
		return "article/articleCreate";
	}
	
	// 작성 로직
	@PostMapping("/article/create")
	@ResponseBody
	public Map<String, String> createArticleApi(@AuthenticationPrincipal EmployeeDetails employeeDetails,
	                                            ArticleDto articleDto,
	                                            @RequestParam("article_files") List<MultipartFile> articleFiles) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "공지 등록 중 오류가 발생했습니다.");
	    
	    Long employeeNo = employeeDetails.getEmployee().getEmployeeNo();

	    int result = articleService.createArticle(articleDto, employeeNo,articleFiles);
	    
	    if (result > 0) {
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "공지 작성 완료되었습니다.");
	    }

	    return resultMap;
	}
}
