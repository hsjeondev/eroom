package com.eroom.project.controller;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.drive.service.DriveService;
import com.eroom.project.service.ProjectService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectFileController {
	
	private final DriveService driveService;
	private final DriveRepository driveRepository;
	private final ProjectService projectService;
	// 파일 저장 경로 
			 @Value("${ffupload.location}")
			 private String fileDir;
			 
			 @PostMapping("/fileUpload")
				@ResponseBody
				public Map<String,String> uploadTeamDriveFiles(DriveDto driveDto,
				                                               @RequestParam("driveDescriptions") List<String> driveDescriptions,
				                                               @AuthenticationPrincipal EmployeeDetails user,
				                                               @RequestParam("fileCategory") String category) {
					
					System.out.println("driveDto" + driveDto);
					
				    Map<String, String> resultMap = new HashMap<>();
				    resultMap.put("res_code", "500");
				    resultMap.put("res_msg", "업로드 실패");

				    driveDto.setDriveDescriptions(driveDescriptions);
				    
				    String separatorCode = "FL006";
				    
				    if(category.equals("1")) {
				    	separatorCode = "FL0061";
				    } else if(category.equals("2")) {
				    	separatorCode = "FL0062";	    	
				    } else if(category.equals("3")) {
				    	separatorCode = "FL0063";	    	
				    }

				    driveDto.setSeparatorCode(separatorCode);

				    int result = projectService.uploadProjectFiles(driveDto, user.getEmployee().getEmployeeNo());

				    if(result > 0) {
				        resultMap.put("res_code", "200");
				        resultMap.put("res_msg", "업로드 성공");
				    }
				    return resultMap;
				}
	
	@GetMapping("/download/{driveAttachNo}")
	public ResponseEntity<Object> downloadTeamDriveFile(@PathVariable("driveAttachNo") Long id,
	                                                    @AuthenticationPrincipal EmployeeDetails user) {
	    try {
	        // DB에서 파일 정보 가져오기
	        Drive drive = driveService.findByDriveAttachNo(id);
	        if (drive == null) {
	            System.out.println("해당 ID로 파일을 찾을 수 없음");
	            return ResponseEntity.notFound().build();
	        }

	        // 실제 파일 경로
	        Path filePath = Paths.get(fileDir + drive.getDrivePath());
	        System.out.println("파일 경로: " + filePath.toString());
	        if (!Files.exists(filePath)) {
	            return ResponseEntity.notFound().build();
	        }

	        // 파일명 한글 깨짐 방지
	        String encodedFileName = URLEncoder.encode(drive.getDriveOriName(), "UTF-8").replaceAll("\\+", "%20");

	        // 응답 생성
	        Resource resource = new InputStreamResource(Files.newInputStream(filePath));
	        return ResponseEntity.ok()
	                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
	                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
	                .body(resource);

	    } catch (Exception e) {
	        System.out.println("팀 드라이브 파일 다운로드 중 예외 발생: " + e.getMessage());
	        e.printStackTrace();
	        return ResponseEntity.badRequest().build();
	    }
	}
	
	@DeleteMapping("/delete/{attachNo}")
	@ResponseBody
	public Map<String, String> deleteTeamDriveFile(@PathVariable("attachNo") Long driveAttachNo,
			@AuthenticationPrincipal EmployeeDetails user) {
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "삭제 실패");
		
		Optional<Drive> optionalDrive = driveRepository.findById(driveAttachNo);
		
		if (optionalDrive.isPresent()) {
			Drive drive = optionalDrive.get();
						
			// 파일 삭제 로직
			int deleteResult = driveService.deleteDriveFile(driveAttachNo);
			if (deleteResult > 0) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "삭제 성공");
			} else {
				resultMap.put("res_msg", "삭제 실패");
			}
		} else {
			resultMap.put("res_msg", "해당 파일을 찾을 수 없습니다.");
		}
		
		return resultMap;
	}
	
	@PostMapping("/download/bulk")
	public ResponseEntity<Resource> bulkDownloadTeam(@RequestParam("fileIds") List<Long> fileIds) {
	    try {
	        Path zipPath = Files.createTempFile("team-bulk-download-", ".zip");
	        Set<String> entryNames = new HashSet<>();

	        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
	            for (Long id : fileIds) {
	                Drive drive = driveService.findByDriveAttachNo(id);
	                if (drive == null) continue;

	                Path filePath = Paths.get(fileDir + drive.getDrivePath());
	                if (!Files.exists(filePath)) continue;

	                String zipEntryName = drive.getDriveOriName();
	                if (entryNames.contains(zipEntryName)) {
	                    int count = 1;
	                    String originalName = zipEntryName;
	                    while (entryNames.contains(zipEntryName)) {
	                        zipEntryName = originalName.replaceFirst("(\\.[^.]+)$", "(" + count + ")$1");
	                        count++;
	                    }
	                }
	                entryNames.add(zipEntryName);

	                zos.putNextEntry(new ZipEntry(zipEntryName));
	                Files.copy(filePath, zos);
	                zos.closeEntry();
	            }
	        }

	        Resource resource = new InputStreamResource(Files.newInputStream(zipPath));

	        return ResponseEntity.ok()
	                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"project-bulk-download.zip\"")
	                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
	                .contentLength(Files.size(zipPath))
	                .body(resource);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.badRequest().build();
	    }
	}
	
	@PostMapping("/delete/bulk")
	@ResponseBody
	@Transactional
	public ResponseEntity<Map<String, String>> bulkDeleteTeam(@RequestBody Map<String, List<Long>> requestData,
	                                                          @AuthenticationPrincipal EmployeeDetails user) {
	    List<Long> fileIds = requestData.get("fileIds");

	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "삭제 실패");

	    if (fileIds == null || fileIds.isEmpty()) {
	        resultMap.put("res_msg", "삭제할 파일이 없습니다.");
	        return ResponseEntity.ok(resultMap);
	    }

	    // 삭제 가능한 파일만 골라 담기
	    List<Long> deletableFileIds = new ArrayList<>();

	    for (Long id : fileIds) {
	        Optional<Drive> optionalDrive = driveRepository.findById(id);
	        if (optionalDrive.isPresent()) {
	        	deletableFileIds.add(id);
	        }
	    }

	    if (deletableFileIds.isEmpty()) {
	        resultMap.put("res_msg", "삭제 가능한 파일이 없습니다.");
	        return ResponseEntity.ok(resultMap);
	    }

	    // 실제 파일 삭제
	    int result = driveService.bulkDeleteDriveFiles(deletableFileIds);

	    if (result > 0) {
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "삭제 성공");
	    }

	    return ResponseEntity.ok(resultMap);
	}

}
