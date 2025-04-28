																																																																					package com.eroom.drive.controller;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.drive.service.DriveService;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.service.StructureService;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/drive")
@RequiredArgsConstructor
public class DriveController {

	private final DriveService driveService;
	private final StructureService structureService;
	private final DriveRepository driveRepository;
	
	// 파일 저장 경로 
		 @Value("${ffupload.location}")
		 private String fileDir;
	
	
	// ------------------------------------------ 드라이브 메인 ------------------------------------------
	// 회사 드라이브 
	@GetMapping("/company")
	public String selectDriveCompany() {
		return "drive/company";
	}
	// 부서 드라이브
	@GetMapping("/department")
	public String selectDriveDepartment() {
		return "drive/department";
	}
	// 팀 드라이브 
	@GetMapping("/team")
	public String selectDriveTeam(@AuthenticationPrincipal EmployeeDetails user, Model model) {
		 Long employeeStructureNo = user.getEmployee().getStructure().getStructureNo();
		 // 내 팀 이름 가져오기
		 Structure team = structureService.getStructureById(employeeStructureNo);
			if (team == null || team.getParentCode() == null) {
				// 부모 코드가 없으면
				return "error";
			}
		// 이 팀의 파일만 조회
			List<DriveDto> fileList = driveService.findTeamDriveFiles(team.getSeparatorCode());
			
			model.addAttribute("fileList", fileList);
			model.addAttribute("teamName", team.getCodeName());
			
			return "drive/team";
		
	}
	// 개인 드라이브
	@GetMapping("/personal")
	public String selectDrivePersonal(Model model, @AuthenticationPrincipal EmployeeDetails user) {
		Long employeeNo = user.getEmployee().getEmployeeNo();
	    List<DriveDto> fileList = driveService.findPersonalDriveFiles(employeeNo);
	    model.addAttribute("fileList", fileList);
		return "drive/personal";
	}
	// ------------------------------------------ 파일 업로드 ------------------------------------------
	// 개인 드라이브 파일 업로드
	@PostMapping("/upload/personal")
	@ResponseBody
	public Map<String,String> uploadPersonalDriveFiles(DriveDto driverDto,@RequestParam("driveDescriptions") List<String> driveDescriptions, @AuthenticationPrincipal EmployeeDetails user){
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "업로드 실패");
		
		driverDto.setDriveDescriptions(driveDescriptions);
		int result = driveService.uploadDriveFiles(driverDto,user.getEmployee().getEmployeeNo());
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "업로드 성공");
		} 
		return resultMap;
	}
	// 팀 드라이브 파일 업로드
	@PostMapping("/upload/team")
	@ResponseBody
	public Map<String,String> uploadTeamDriveFiles(DriveDto driveDto,
	                                               @RequestParam("driveDescriptions") List<String> driveDescriptions,
	                                               @AuthenticationPrincipal EmployeeDetails user) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "업로드 실패");

	    driveDto.setDriveDescriptions(driveDescriptions);

	    // 로그인한 사용자의 팀 separator_code를 가져온다
	    String myTeamSeparatorCode = user.getEmployee().getStructure().getSeparatorCode(); 

	    // 팀 드라이브니까 separatorCode를 강제로 설정
	    driveDto.setSeparatorCode(myTeamSeparatorCode);

	    // param1에도 structureNo (구조번호) 저장해줄 수 있어
	    driveDto.setParam1(user.getEmployee().getStructure().getStructureNo());

	    int result = driveService.uploadTeamDriveFiles(driveDto, user.getEmployee().getEmployeeNo());

	    if(result > 0) {
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "업로드 성공");
	    }
	    return resultMap;
	}


	
	
	// --------------------------------- 파일 수정 ------------------------------------------
	// 개인 드라이브 파일 수정
	@PostMapping("/update/{attachNo}")
	@ResponseBody
	public Map<String, String> editDriveFile(@PathVariable("attachNo") Long attachNo,
	                                         @RequestParam(value = "driveFile", required = false) MultipartFile file,
	                                         @RequestParam("driveDescription") String description) {
	    Map<String, String> result = new HashMap<>();
	    result.put("res_code", "500");
	    result.put("res_msg", "수정 실패");

	    boolean success = driveService.updateDriveFile(attachNo, file, description);
	    if (success) {
	        result.put("res_code", "200");
	        result.put("res_msg", "수정 완료");
	    }

	    return result;
	}
	// 팀 드라이브 파일 수정
	@PostMapping("/update/team/{attachNo}")
	@ResponseBody
	public Map<String, String> editTeamDriveFile(@PathVariable("attachNo") Long attachNo,
	                                             @RequestParam(value = "driveFile", required = false) MultipartFile file,
	                                             @RequestParam("driveDescription") String description,
	                                             @AuthenticationPrincipal EmployeeDetails user) {
	    Map<String, String> result = new HashMap<>();
	    result.put("res_code", "500");
	    result.put("res_msg", "수정 실패");

	    // 현재 로그인한 사용자의 팀 정보 가져오기
	    String separatorCode  = user.getEmployee().getStructure().getSeparatorCode(); 
	    String driveEditor = user.getEmployee().getEmployeeName(); // 수정자 이름

	    boolean success = driveService.updateTeamDriveFile(attachNo, file, description, separatorCode, driveEditor);
	    if (success) {
	        result.put("res_code", "200");
	        result.put("res_msg", "수정 완료");
	    }

	    return result;
	}

	
	// ---------------------------------- 파일 다운로드 ------------------------------------------
	// 개인 파일 다운로드
	@GetMapping("/download/personal/{driveAttachNo}")
	public ResponseEntity<Object> fileDownload(@PathVariable("driveAttachNo") Long id) {
		System.out.println("다운로드 시도: " + id);
		try {
			// DB에서 파일 정보 가져오기
			Drive drive = driveService.findByDriveAttachNo(id);
			if (drive == null) {
			    System.out.println("해당 ID로 파일을 찾을 수 없음");
			    return ResponseEntity.notFound().build();
			}
			// 실제 파일 경로
			System.out.println("drive.getDrivePath(): " + drive.getDrivePath());
			Path filePath = Paths.get(fileDir + drive.getDrivePath());
			System.out.println("파일 경로: " + filePath.toString());
			System.out.println("파일 존재 여부: " + Files.exists(filePath));
			if(!Files.exists(filePath)) {
				return ResponseEntity.notFound().build();
			}
			// 파일명 한글 깨짐 방지
			String encodedFileName = URLEncoder.encode(drive.getDriveOriName(), "UTF-8")
											   .replaceAll("\\+", "%20");
            // 응답 생성
			Resource resource = new InputStreamResource(Files.newInputStream(filePath));
			// 다운로드 응답
			return ResponseEntity.ok()
					.header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
					.contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
					.body(resource);
			
		} catch (Exception e) {
			System.out.println("파일 다운로드 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
	// 개인 파일 일괄 다운로드
	@PostMapping("/download/personal/bulk")
	public ResponseEntity<Resource> bulkDownload(@RequestParam("fileIds") List<Long> fileIds) {
	    try {
	        // 임시 zip 파일 생성
	        Path zipPath = Files.createTempFile("bulk-download-", ".zip");

	        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
	            for (Long id : fileIds) {
	                Drive drive = driveService.findByDriveAttachNo(id);
	                if (drive == null) continue;

	                Path filePath = Paths.get(fileDir + drive.getDrivePath());
	                if (!Files.exists(filePath)) continue;

	                // ZIP 안에 들어갈 파일 이름
	                String zipEntryName = drive.getDriveOriName();
	                zos.putNextEntry(new ZipEntry(zipEntryName));
	                Files.copy(filePath, zos);
	                zos.closeEntry();
	            }
	        }
	        // zip 파일 리소스화
	        Resource resource = new InputStreamResource(Files.newInputStream(zipPath));

	        return ResponseEntity.ok()
	                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bulk-download.zip\"")
	                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
	                .contentLength(Files.size(zipPath))
	                .body(resource);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.badRequest().build();
	    }
	}

	
	
	// -------------------------------------------- 파일 삭제 ------------------------------------------
	// 개인 드라이브 파일 삭제
	@DeleteMapping("/delete/{attachNo}")
	@ResponseBody
	public Map<String, String> deleteDriveFile(@PathVariable("attachNo") Long driveAttachNo) {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "삭제 실패");
		System.out.println("Deleting file with attachNo: " + driveAttachNo);
		int result = driveService.deleteDriveFile(driveAttachNo);

		if (result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "삭제 성공");
		}

		return resultMap;
	}
	// 개인 드라이브 파일 일괄 삭제
	@PostMapping("/delete/personal/bulk")
	@ResponseBody
	@Transactional
	public ResponseEntity<Map<String, String>> bulkDelete(@RequestBody Map<String, List<Long>> requestData) {
	    // JSON에서 fileIds를 추출
	    List<Long> fileIds = requestData.get("fileIds");

	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "삭제 실패");

	    // 받은 fileIds를 콘솔에 출력 (디버깅 용도)
	    System.out.println("Received fileIds: " + fileIds);

	    // 파일 삭제 서비스 호출
	    int result = driveService.bulkDeleteDriveFiles(fileIds);

	    if (result > 0) {
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "삭제 성공");
	    }

	    return ResponseEntity.ok(resultMap);
	}
	// 팀 드라이브 파일 삭제
	@DeleteMapping("/delete/team/{attachNo}")
	@ResponseBody
	public Map<String, String> deleteTeamDriveFile(@PathVariable("attachNo") Long driveAttachNo,
	                                               @AuthenticationPrincipal EmployeeDetails user) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "삭제 실패");

	    // 현재 로그인한 사용자의 팀 정보 가져오기
	    String separatorCode = user.getEmployee().getStructure().getSeparatorCode(); // 로그인한 사용자의 팀 정보
	    Optional<Drive> optionalDrive = driveRepository.findById(driveAttachNo);

	    if (optionalDrive.isPresent()) {
	        Drive drive = optionalDrive.get();

	        // 파일의 separatorCode(팀 코드)와 로그인한 사용자의 팀 코드가 동일한지 확인
	        if (!drive.getSeparatorCode().equals(separatorCode)) {
	            resultMap.put("res_msg", "팀 소속이 아니므로 삭제할 수 없습니다.");
	            return resultMap;  // 다른 팀의 파일은 삭제할 수 없음
	        }

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



	
	
	// 결재 파일 다운로드 - 개인 다운로드 기능 그대로 가져왔는데 개인 다운로드 기능 변화 없으면 병합 필요(결재의 detail.html a태그 링크만 바꾸면 됨)
	@GetMapping("/download/approval/{driveAttachNo}")
	public ResponseEntity<Object> approvalFileDownload(@PathVariable("driveAttachNo") Long id) {
//		System.out.println("다운로드 시도: " + id);
		try {
			// DB에서 파일 정보 가져오기
			Drive drive = driveService.findByDriveAttachNo(id);
			if (drive == null) {
			    System.out.println("해당 ID로 파일을 찾을 수 없음");
			    return ResponseEntity.notFound().build();
			}
			// 실제 파일 경로
//			System.out.println("drive.getDrivePath(): " + drive.getDrivePath());
//			Path filePath = Paths.get(fileDir + drive.getDrivePath());
			Path filePath = Paths.get(drive.getDrivePath());
//			System.out.println("파일 경로: " + filePath.toString());
//			System.out.println("파일 존재 여부: " + Files.exists(filePath));
			if(!Files.exists(filePath)) {
				return ResponseEntity.notFound().build();
			}
			// 파일명 한글 깨짐 방지
			String encodedFileName = URLEncoder.encode(drive.getDriveOriName(), "UTF-8")
											   .replaceAll("\\+", "%20");
            // 응답 생성
			Resource resource = new InputStreamResource(Files.newInputStream(filePath));
			// 다운로드 응답
			return ResponseEntity.ok()
					.header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
					.contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
					.body(resource);
			
		} catch (Exception e) {
			System.out.println("파일 다운로드 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
}
