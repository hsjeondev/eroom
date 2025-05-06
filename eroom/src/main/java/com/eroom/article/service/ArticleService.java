package com.eroom.article.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.article.dto.ArticleDto;
import com.eroom.article.entity.Article;
import com.eroom.article.repository.ArticleRepository;
import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
	
	 private final ArticleRepository articleRepository;
	 private final EmployeeRepository employeeRepository;
	 private final DriveRepository driveRepository;
	 @Value("${ffupload.location}")
	 private String fileDir;
	 
	 
	 // 게시글 삭제
	 public void deleteArticleNotice(Long articleNo) {
		 articleRepository.updateArticleVisibilityStatus(articleNo, "N");  // Repository 메소드 호출
	    }
	 
	 
	 // 게시판 다운로드 파일 정보
	 public Drive findAttachmentById(Long driveId) {
		    return driveRepository.findById(driveId)
		            .orElse(null);
		}
	 
	 // 디테일 파일 조회
	 public List<Drive> findArticleAttaNoticechments(Long articleId) {
		    return driveRepository.findBySeparatorCodeAndParam1AndVisibleYn("FL004", articleId, "Y");
		}
		
	 
	 // 공지 디테일 
	 public Article selectArticleNoticeOne(Long articleId) {
	        return articleRepository.findByArticleNoAndVisibleYn(articleId, "Y")
	                .orElseThrow(() -> new EntityNotFoundException("존재하지 않거나 비공개된 공지사항입니다."));
	    }
	 
	 // 공지 게시판 조회
	 public List<Article> getNoticeArticles() {
	        return articleRepository.findBySeparatorCodeAndVisibleYnOrderByArticleRegDateDesc("B001", "Y");
	    }
	 
	 // 공지 게시판 작성 
	 public int createArticle(ArticleDto articleDto, Long employeeNo, List<MultipartFile> articleFiles) {
		 int result = 0;
	        Employee employee = employeeRepository.findById(employeeNo)
	                .orElseThrow(() -> new IllegalArgumentException("해당 사번의 사용자를 찾을 수 없습니다."));
	        try {
	
	        Article articleEntity = articleDto.toEntity();
	        articleEntity.setArticleRegDate(LocalDateTime.now());
			Article articleSaver = articleRepository.save(articleEntity);
			
			for (MultipartFile file : articleFiles) {
	            if (!file.isEmpty()) {
	                // DriveDto 생성
	                String oriName = file.getOriginalFilename();
			        String ext = oriName.substring(oriName.lastIndexOf("."));
			        String newName = UUID.randomUUID().toString().replace("-", "") + ext;
			        String path = fileDir + "article/notice" + newName;
	                DriveDto driveDto = new DriveDto();
	                
	                File savedFile = new File(path);
			        if (!savedFile.getParentFile().exists()) {
			            savedFile.getParentFile().mkdirs();
			        }
			        file.transferTo(savedFile);
			        
			        
	                driveDto.setDriveOriName(oriName);
	                driveDto.setDriveSize(file.getSize());
	                
	                
	               
	                // Drive 엔티티로 변환
	                Drive drive = new Drive();
	                drive.setDriveOriName(driveDto.getDriveOriName());
	                drive.setDriveNewName(newName); // 파일 고유 이름 생성
	                drive.setDriveSize(driveDto.getDriveSize());
	                drive.setDriveType(ext);
	                drive.setDrivePath("article/notice" + newName); // 실제 저장 경로로 변경 필요
	                drive.setUploader(Employee.builder().employeeNo(articleDto.getEmployee_no()).build());
	                drive.setParam1(articleSaver.getArticleNo()); // 메일 참조 연결
	                drive.setSeparatorCode("FL004");
	                drive.setVisibleYn("Y");
	                // DB에 저장
	                driveRepository.save(drive);
	                
	            }
	        }
			result = 1;
//	        Article article = Article.builder()
//	                .employee(employee)
//	                .articleTitle(articleDto.getArticle_title())
//	                .articleContent(articleDto.getArticle_content())
//	                .separatorCode("B001")
//	                .order(articleDto.getOrder())
//	                .visibleYn("Y")
//	                .articleCreator(employeeNo)
//	                .articleEditor(employeeNo)
//	                .articleRegDate(LocalDateTime.now())
//	                // .articleModDate(LocalDateTime.now())
//	                .build();
//			articleRepository.save(article);
	        }catch (Exception e) {
				e.printStackTrace();
			}
	        return result;
	 }
}