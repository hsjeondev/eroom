package com.eroom.article.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
	 
	 public List<Article> test2(){
		 return articleRepository.findByArticleEmergencyYnAndVisibleYnOrderByArticleRegDateDesc("Y", "Y");
	 }
	 
	 
	 // 게시글 수정 파일 삭제
	 @Transactional
	 public void markAsDeleted(Long attachNo) {
	     Drive drive = driveRepository.findById(attachNo)
	             .orElseThrow(() -> new IllegalArgumentException("해당 첨부파일을 찾을 수 없습니다."));

	     drive.setVisibleYn("N");
	     driveRepository.save(drive); 
	 }
	 
	 // 게시글 수정
	 @Transactional
	 public int updateArticle(ArticleDto articleDto, List<MultipartFile> articleFiles) {
	     int result = 0;

	     try {
	         Article article = articleRepository.findById(articleDto.getArticle_no())
	                 .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

	         // 게시글 정보 업데이트
	         article.setArticleEmergencyYn(articleDto.getArticle_emergency_yn());
	         article.setArticleTitle(articleDto.getArticle_title());
	         article.setArticleContent(articleDto.getArticle_content());
	         article.setArticleModDate(LocalDateTime.now());  // 수정일 추가

	         // 첨부파일 처리
	         for (MultipartFile file : articleFiles) {
	             if (!file.isEmpty()) {
	                 String oriName = file.getOriginalFilename();
	                 String ext = oriName.substring(oriName.lastIndexOf("."));
	                 String newName = UUID.randomUUID().toString().replace("-", "") + ext;
	                 String path = fileDir + "article/notice/" + newName;

	                 File savedFile = new File(path);
	                 if (!savedFile.getParentFile().exists()) {
	                     savedFile.getParentFile().mkdirs();
	                 }
	                 file.transferTo(savedFile);

	                 Drive drive = new Drive();
	                 drive.setDriveOriName(oriName);
	                 drive.setDriveNewName(newName);
	                 drive.setDriveSize(file.getSize());
	                 drive.setDriveType(ext);
	                 drive.setDrivePath("article/notice/" + newName);
	                 drive.setUploader(Employee.builder().employeeNo(articleDto.getEmployee_no()).build());
	                 drive.setParam1(article.getArticleNo());
	                 drive.setSeparatorCode("FL004");
	                 drive.setVisibleYn("Y");

	                 driveRepository.save(drive);
	             }
	         }

	         result = 1;
	     } catch (Exception e) {
	         e.printStackTrace();
	         throw new RuntimeException("게시글 수정 중 오류 발생", e); // 예외를 다시 던져야 트랜잭션도 롤백됨
	     }

	     return result;
	 }
	 
	 
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
	        return articleRepository.findNoticeBoard("B001", "Y");
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
			        String path = fileDir + "article/notice/" + newName;
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
	                drive.setDrivePath("article/notice/" + newName); // 실제 저장 경로로 변경 필요
	                drive.setUploader(Employee.builder().employeeNo(articleDto.getEmployee_no()).build());
	                drive.setParam1(articleSaver.getArticleNo()); // 메일 참조 연결
	                drive.setSeparatorCode("FL004");
	                drive.setVisibleYn("Y");
	                // DB에 저장
	                driveRepository.save(drive);
	                
	            }
	        }
			result = 1;
	        }catch (Exception e) {
				e.printStackTrace();
			}
	        return result;
	 }
}