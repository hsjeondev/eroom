																																																																					package com.eroom.drive.controller;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.eroom.employee.entity.Separator;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.repository.SeparatorRepository;
import com.eroom.employee.repository.StructureRepository;
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
	private final SeparatorRepository separatorRepository;
	private final StructureRepository structureRepository;
	// 파일 저장 경로 
		 @Value("${ffupload.location}")
		 private String fileDir;
	
	
	// ------------------------------------------ 드라이브 메인 ------------------------------------------
	// 회사 드라이브
	 @GetMapping("/company")
	 public String selectDriveCompany(Model model, @AuthenticationPrincipal EmployeeDetails user) {
	     List<DriveDto> fileList = driveService.findCompanyDriveFiles(); // 회사 드라이브 조회
	     
	     model.addAttribute("fileList", fileList);
	     model.addAttribute("pageTitle", "회사 드라이브");
	     model.addAttribute("user", user);
	     
	     return "drive/company";
	 }

	// 부서 드라이브
	 @GetMapping("/department")
	 public String selectDriveDepartment(@AuthenticationPrincipal EmployeeDetails user, Model model) {
	     String departmentCode = user.getEmployee().getStructure().getParentCode(); // D004 등

	     if (departmentCode == null) return "error";

	     List<DriveDto> fileList = driveService.findDepartmentDriveFiles(departmentCode); // ✅ 핵심 변경

	     model.addAttribute("fileList", fileList);
	     model.addAttribute("departmentName", structureService.getBySeparatorCode(departmentCode).getCodeName());
	     model.addAttribute("user", user);

	     return "drive/department";
	 }

	// 팀 드라이브 
	@GetMapping("/team")
	public String selectDriveTeam(@AuthenticationPrincipal EmployeeDetails user, Model model) {
	    Long employeeStructureNo = user.getEmployee().getStructure().getStructureNo();
	    Structure team = structureService.getStructureById(employeeStructureNo);

	 // 팀 조건: 상위 조직이 존재해야 함 (즉, parent_code != null)
	 if (team == null || team.getParentCode() == null) {
	     model.addAttribute("message", "팀에 소속된 사용자만 팀 드라이브에 접근할 수 있습니다.");
	     return "error/custom-message";
	 }


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
	    model.addAttribute("userName", user.getEmployee().getEmployeeName());
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

	    int result = driveService.uploadTeamDriveFiles(driveDto, user.getEmployee().getEmployeeNo());

	    if(result > 0) {
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "업로드 성공");
	    }
	    return resultMap;
	}
	// 부서 드라이브 파일 업로드
	@PostMapping("/upload/department")
	@ResponseBody
	public Map<String,String> uploadDepartmentFiles(DriveDto driveDto,
										            @RequestParam("driveDescriptions") List<String> driveDescriptions,
										            @AuthenticationPrincipal EmployeeDetails user){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "업로드 실패");
		
		driveDto.setDriveDescriptions(driveDescriptions);
		
		String myTeamSeparatorCode = user.getEmployee().getStructure().getSeparatorCode();
		
//		Separator separator = separatorRepository.findBySeparatorCode(myTeamSeparatorCode)
//			     .orElseThrow(() -> new IllegalArgumentException("해당 separatorCode를 찾을 수 없습니다: " + myTeamSeparatorCode));
//		System.out.println("세퍼레이터 " + separator);
		Structure structure = structureRepository.findBySeparatorCode(myTeamSeparatorCode);
		System.out.println("진짜 될까요? : " + structure);
		String myDepartmentSeparatorCode = structure.getParentCode();
		
		driveDto.setSeparatorCode(myDepartmentSeparatorCode);

		
		int result = driveService.uploadDepartmentDriveFiles(driveDto, user.getEmployee().getEmployeeNo());
		
		if (result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "업로드 성공");
		}
		return resultMap;
		
	}
	// 회사 드라이브 파일 업로드
	@PostMapping("/upload/company")
	@ResponseBody
	public Map<String, String> uploadCompanyDriveFiles(
	        DriveDto driveDto,
	        @RequestParam("driveDescriptions") List<String> driveDescriptions,
	        @AuthenticationPrincipal EmployeeDetails user) {
	    
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "업로드 실패");
	    
	    driveDto.setDriveDescriptions(driveDescriptions);

	    driveDto.setSeparatorCode("A001");

	    int result = driveService.uploadCompanyDriveFiles(driveDto, user.getEmployee().getEmployeeNo());

	    if (result > 0) {
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
	// 부서 드라이브 파일 수정
	@PostMapping("/update/department/{attachNo}")
	@ResponseBody
	public Map<String, String> editDepartmentDriveFile(@PathVariable("attachNo") Long attachNo,
	                                                   @RequestParam(value = "driveFile", required = false) MultipartFile file,
	                                                   @RequestParam("driveDescription") String description,
	                                                   @AuthenticationPrincipal EmployeeDetails user) {
	    Map<String, String> result = new HashMap<>();
	    result.put("res_code", "500");
	    result.put("res_msg", "수정 실패");

	    // 부서코드랑 수정자 이름 가져오기
	    String separatorCode = user.getEmployee().getStructure().getParentCode(); // 팀 상위 = 부서
	    String driveEditor = user.getEmployee().getEmployeeName();
	    Long currentEmployeeNo = user.getEmployee().getEmployeeNo();
	    
	    boolean success = driveService.updateDepartmentDriveFile(attachNo, file, description, separatorCode, driveEditor,currentEmployeeNo);
	    if (success) {
	        result.put("res_code", "200");
	        result.put("res_msg", "수정 완료");
	    }

	    return result;
	}
	// 회사 드라이브 파일 수정
	@PostMapping("/update/company/{attachNo}")
	@ResponseBody
	public Map<String, String> editCompanyDriveFile(@PathVariable("attachNo") Long attachNo,
	                                                @RequestParam(value = "driveFile", required = false) MultipartFile file,
	                                                @RequestParam("driveDescription") String description,
	                                                @AuthenticationPrincipal EmployeeDetails user) {
	    Map<String, String> result = new HashMap<>();
	    result.put("res_code", "500");
	    result.put("res_msg", "수정 실패");

	    String separatorCode = "A001";
	    String driveEditor = user.getEmployee().getEmployeeName();
	    Long currentEmployeeNo = user.getEmployee().getEmployeeNo();

	    boolean success = driveService.updateCompanyDriveFile(attachNo, file, description, separatorCode, driveEditor, currentEmployeeNo);

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
	// 팀 파일 다운로드
	@GetMapping("/download/team/{driveAttachNo}")
	public ResponseEntity<Object> downloadTeamDriveFile(@PathVariable("driveAttachNo") Long id,
	                                                    @AuthenticationPrincipal EmployeeDetails user) {
	    System.out.println("팀 드라이브 다운로드 시도: " + id);
	    try {
	        // DB에서 파일 정보 가져오기
	        Drive drive = driveService.findByDriveAttachNo(id);
	        if (drive == null) {
	            System.out.println("해당 ID로 파일을 찾을 수 없음");
	            return ResponseEntity.notFound().build();
	        }

	        // 로그인한 사용자의 팀 코드
	        String userSeparatorCode = user.getEmployee().getStructure().getSeparatorCode();

	        // 파일 소속 팀 코드와 현재 로그인한 유저 팀 코드 일치 확인
	        if (!drive.getSeparatorCode().equals(userSeparatorCode)) {
	            System.out.println("팀이 다릅니다. 다운로드 불가");
	            return ResponseEntity.status(403).body("해당 팀 파일만 다운로드할 수 있습니다.");
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
	// 부서 드라이브 파일 다운로드
	@GetMapping("/download/department/{driveAttachNo}")
	public ResponseEntity<Object> downloadDepartmentDriveFile(@PathVariable("driveAttachNo") Long id,
	                                                           @AuthenticationPrincipal EmployeeDetails user) {
	    System.out.println("부서 드라이브 다운로드 시도: " + id);
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
	        System.out.println("부서 드라이브 파일 다운로드 중 예외 발생: " + e.getMessage());
	        e.printStackTrace();
	        return ResponseEntity.badRequest().build();
	    }
	}
	// 회사 드라이브 파일 다운로드
	@GetMapping("/download/company/{driveAttachNo}")
	public ResponseEntity<Object> downloadCompanyDriveFile(@PathVariable("driveAttachNo") Long id) {
	    System.out.println("회사 드라이브 다운로드 시도: " + id);
	    try {
	        // DB에서 파일 정보 가져오기
	        Drive drive = driveService.findByDriveAttachNo(id);
	        if (drive == null) {
	            System.out.println("해당 ID로 파일을 찾을 수 없음");
	            return ResponseEntity.notFound().build();
	        }

	        // separatorCode가 회사(A001)인지 체크
	        if (!"A001".equals(drive.getSeparatorCode())) {
	            System.out.println("회사 파일이 아님");
	            return ResponseEntity.status(403).body("회사 파일만 다운로드할 수 있습니다.");
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
	        System.out.println("회사 드라이브 파일 다운로드 중 예외 발생: " + e.getMessage());
	        e.printStackTrace();
	        return ResponseEntity.badRequest().build();
	    }
	}
	
	// 개인 파일 일괄 다운로드
	@PostMapping("/download/personal/bulk")
	public ResponseEntity<Resource> bulkDownload(@RequestParam("fileIds") List<Long> fileIds) {
	    try {
	        Path zipPath = Files.createTempFile("bulk-download-", ".zip");
	        Set<String> entryNames = new HashSet<>(); // 추가

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
	                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bulk-download.zip\"")
	                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
	                .contentLength(Files.size(zipPath))
	                .body(resource);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.badRequest().build();
	    }
	}

	// 팀 파일 일괄 다운로드
	@PostMapping("/download/team/bulk")
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
	                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"team-bulk-download.zip\"")
	                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
	                .contentLength(Files.size(zipPath))
	                .body(resource);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.badRequest().build();
	    }
	}
	// 부서 드라이브 파일 일괄 다운로드
	@PostMapping("/download/department/bulk")
	public ResponseEntity<Resource> bulkDownloadDepartment(@RequestParam("fileIds") List<Long> fileIds) {
	    try {
	        Path zipPath = Files.createTempFile("department-bulk-download-", ".zip");
	        Set<String> entryNames = new HashSet<>();

	        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
	            for (Long id : fileIds) {
	                Drive drive = driveService.findByDriveAttachNo(id);  // id로 파일 찾기
	                if (drive == null) continue;

	                Path filePath = Paths.get(fileDir + drive.getDrivePath());  // 실제 파일 경로
	                if (!Files.exists(filePath)) continue;

	                String zipEntryName = drive.getDriveOriName();  // Zip 안에 보이는 파일명
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
	                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"department-bulk-download.zip\"")
	                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
	                .contentLength(Files.size(zipPath))
	                .body(resource);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.badRequest().build();
	    }
	}
	// 회사 드라이브 파일 일괄 다운로드
	@PostMapping("/download/company/bulk")
	public ResponseEntity<Resource> bulkDownloadCompany(@RequestParam("fileIds") List<Long> fileIds) {
	    try {
	        Path zipPath = Files.createTempFile("department-bulk-download-", ".zip");
	        Set<String> entryNames = new HashSet<>();

	        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
	            for (Long id : fileIds) {
	                Drive drive = driveService.findByDriveAttachNo(id);  // id로 파일 찾기
	                if (drive == null) continue;

	                Path filePath = Paths.get(fileDir + drive.getDrivePath());  // 실제 파일 경로
	                if (!Files.exists(filePath)) continue;

	                String zipEntryName = drive.getDriveOriName();  // Zip 안에 보이는 파일명
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
	                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"department-bulk-download.zip\"")
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
	// 부서 드라이브 파일 삭제
	@DeleteMapping("/delete/department/{attachNo}")
	@ResponseBody
	public Map<String, String> deleteDepartmentDriveFile(@PathVariable("attachNo") Long driveAttachNo,
	                                                     @AuthenticationPrincipal EmployeeDetails user) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "삭제 실패");

	    // 현재 로그인한 사용자의 부서 코드 가져오기
	    String departmentSeparatorCode = user.getEmployee().getStructure().getParentCode(); // 부서 코드
	    Optional<Drive> optionalDrive = driveRepository.findById(driveAttachNo);

	    if (optionalDrive.isPresent()) {
	        Drive drive = optionalDrive.get();
	        
	        // 파일의 부서 separatorCode와 로그인한 사용자의 부서 separatorCode가 일치하는지 확인
	        if (!drive.getSeparatorCode().equals(departmentSeparatorCode)) {
	            resultMap.put("res_msg", "부서 소속이 아니므로 삭제할 수 없습니다.");
	            return resultMap;
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
	// 회사 드라이브 파일 삭제
	@DeleteMapping("/delete/company/{attachNo}")
	@ResponseBody
	public Map<String, String> deleteCompanyDriveFile(@PathVariable("attachNo") Long driveAttachNo) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "삭제 실패");

	    Optional<Drive> optionalDrive = driveRepository.findById(driveAttachNo);

	    if (optionalDrive.isPresent()) {
	        Drive drive = optionalDrive.get();
	        
	        // 파일의 separatorCode가 "A001" (회사코드) 인지 체크
	        if (!"A001".equals(drive.getSeparatorCode())) {
	            resultMap.put("res_msg", "회사 드라이브 파일이 아닙니다.");
	            return resultMap;
	        }

	        // 삭제 시도
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
	// 팀 드라이브 파일 일괄 삭제
	@PostMapping("/delete/team/bulk")
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

	    // 로그인한 사용자의 팀 separatorCode
	    String mySeparatorCode = user.getEmployee().getStructure().getSeparatorCode();

	    // 삭제 가능한 파일만 골라 담기
	    List<Long> deletableFileIds = new ArrayList<>();

	    for (Long id : fileIds) {
	        Optional<Drive> optionalDrive = driveRepository.findById(id);
	        if (optionalDrive.isPresent()) {
	            Drive drive = optionalDrive.get();
	            if (mySeparatorCode.equals(drive.getSeparatorCode())) {
	                deletableFileIds.add(id);
	            }
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

	// 회사 드라이브 파일 일괄 삭제
	@PostMapping("/delete/company/bulk")
	@ResponseBody
	@Transactional
	public ResponseEntity<Map<String, String>> bulkDeleteCompany(@RequestBody Map<String, List<Long>> requestData) {
	    List<Long> fileIds = requestData.get("fileIds");

	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "삭제 실패");

	    if (fileIds == null || fileIds.isEmpty()) {
	        resultMap.put("res_msg", "삭제할 파일이 없습니다.");
	        return ResponseEntity.ok(resultMap);
	    }

	    List<Long> deletableFileIds = new ArrayList<>();

	    for (Long id : fileIds) {
	        Optional<Drive> optionalDrive = driveRepository.findById(id);
	        if (optionalDrive.isPresent()) {
	            Drive drive = optionalDrive.get();
	            if ("A001".equals(drive.getSeparatorCode())) {
	                deletableFileIds.add(id);
	            }
	        }
	    }

	    if (deletableFileIds.isEmpty()) {
	        resultMap.put("res_msg", "삭제 가능한 파일이 없습니다.");
	        return ResponseEntity.ok(resultMap);
	    }

	    int result = driveService.bulkDeleteDriveFiles(deletableFileIds);

	    if (result > 0) {
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "삭제 성공");
	    }

	    return ResponseEntity.ok(resultMap);
	}
	// ------------------------- 드라이브 세부정보 카드 통계 -------------------------------
	@GetMapping("/summary/personal")
	@ResponseBody
	public Map<String, Object> getPersonalDriveSummary(@AuthenticationPrincipal EmployeeDetails user) {
	    Long employeeNo = user.getEmployee().getEmployeeNo();
	    String separatorCode = "E001"; // 개인 드라이브 구분자

	    List<Drive> drives = driveRepository.findByUploader_EmployeeNoAndSeparatorCodeAndVisibleYn(employeeNo, separatorCode, "Y");

	    long totalFiles = 0;
	    long totalDownloads = 0;
	    long totalSizeKB = 0;
	    LocalDateTime recentDate = null;

	    // 파일 타입별 크기 (KB단위로 모은다)
	    Map<String, Long> fileTypeSizeMap = new HashMap<>();
	    fileTypeSizeMap.put("pdf", 0L);
	    fileTypeSizeMap.put("image", 0L); // jpg, png
	    fileTypeSizeMap.put("docx", 0L);
	    fileTypeSizeMap.put("xlsx", 0L);
	    fileTypeSizeMap.put("zip", 0L);
	    fileTypeSizeMap.put("etc", 0L);

	    for (Drive drive : drives) {
	        totalFiles++;
	        totalDownloads += drive.getDownloadCount();
	        totalSizeKB += drive.getDriveSize(); // KB 단위 저장

	        String type = drive.getDriveType();
	        if (type == null) type = "";
	        type = type.startsWith(".") ? type.toLowerCase() : "." + type.toLowerCase();
	        
	        switch (type.toLowerCase()) {
	            case ".pdf":
	                fileTypeSizeMap.put("pdf", fileTypeSizeMap.get("pdf") + drive.getDriveSize());
	                break;
	            case ".jpg":
	            case ".png":
	                fileTypeSizeMap.put("image", fileTypeSizeMap.get("image") + drive.getDriveSize());
	                break;
	            case ".docx":
	                fileTypeSizeMap.put("docx", fileTypeSizeMap.get("docx") + drive.getDriveSize());
	                break;
	            case ".xlsx":
	                fileTypeSizeMap.put("xlsx", fileTypeSizeMap.get("xlsx") + drive.getDriveSize());
	                break;
	            case ".zip":
	                fileTypeSizeMap.put("zip", fileTypeSizeMap.get("zip") + drive.getDriveSize());
	                break;
	            default:
	                fileTypeSizeMap.put("etc", fileTypeSizeMap.get("etc") + drive.getDriveSize());
	        }
	        if (recentDate == null || drive.getDriveRegDate().isAfter(recentDate)) {
	            recentDate = drive.getDriveRegDate();
	        }
	    }

	    double totalUsedGB = totalSizeKB / 1024.0 / 1024.0; // KB → MB → GB

	    // 파일 타입별 GB 변환
	    Map<String, Double> fileTypeUsage = new HashMap<>();
	    for (Map.Entry<String, Long> entry : fileTypeSizeMap.entrySet()) {
	        fileTypeUsage.put(entry.getKey(), entry.getValue() / 1024.0 / 1024.0); // KB → GB
	    }

	    Map<String, Object> result = new HashMap<>();
	    result.put("totalFiles", totalFiles);
	    result.put("totalDownloads", totalDownloads);
	    result.put("recentUploadDate", recentDate != null ? recentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "-");
	    result.put("totalUsedGB", totalUsedGB);
	    result.put("fileTypeUsage", fileTypeUsage); 
	    result.put("totalUsedKB", totalSizeKB);
	    return result;
	}
	@GetMapping("/summary/department")
	@ResponseBody
	public Map<String, Object> getDepartmentSummary(@AuthenticationPrincipal EmployeeDetails user) {
	    String departmentCode = user.getEmployee().getStructure().getParentCode();

	    List<String> codeList = structureService.getDepartmentAndTeamSeparatorCodes(departmentCode);
	    codeList.add(departmentCode);

	    List<Drive> drives = driveRepository.findBySeparatorCodeInAndVisibleYn(codeList, "Y");

	    long totalFiles = 0;
	    long totalDownloads = 0;
	    long totalSizeKB = 0;
	    LocalDateTime recentDate = null;

	    Map<String, Long> fileTypeSizeMap = new HashMap<>(Map.of(
	        "pdf", 0L, "image", 0L, "docx", 0L, "xlsx", 0L, "zip", 0L, "etc", 0L
	    ));

	    for (Drive drive : drives) {
	        totalFiles++;
	        totalDownloads += drive.getDownloadCount();
	        totalSizeKB += drive.getDriveSize();

	        String type = Optional.ofNullable(drive.getDriveType()).orElse("").toLowerCase();
	        type = type.startsWith(".") ? type : "." + type;

	        switch (type) {
	            case ".pdf" -> fileTypeSizeMap.compute("pdf", (k, v) -> v + drive.getDriveSize());
	            case ".jpg", ".png" -> fileTypeSizeMap.compute("image", (k, v) -> v + drive.getDriveSize());
	            case ".docx" -> fileTypeSizeMap.compute("docx", (k, v) -> v + drive.getDriveSize());
	            case ".xlsx" -> fileTypeSizeMap.compute("xlsx", (k, v) -> v + drive.getDriveSize());
	            case ".zip" -> fileTypeSizeMap.compute("zip", (k, v) -> v + drive.getDriveSize());
	            default -> fileTypeSizeMap.compute("etc", (k, v) -> v + drive.getDriveSize());
	        }

	        if (recentDate == null || drive.getDriveRegDate().isAfter(recentDate)) {
	            recentDate = drive.getDriveRegDate();
	        }
	    }

	    Map<String, Object> result = new HashMap<>();
	    result.put("totalFiles", totalFiles);
	    result.put("totalDownloads", totalDownloads);
	    result.put("recentUploadDate", recentDate != null ? recentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "-");
	    result.put("totalUsedKB", totalSizeKB);
	    result.put("fileTypeUsage", fileTypeSizeMap);

	    return result;
	}


	@GetMapping("/summary/team")
	@ResponseBody
	public Map<String, Object> getTeamDriveSummary(@AuthenticationPrincipal EmployeeDetails user) {
	    String separatorCode = user.getEmployee().getStructure().getSeparatorCode();
	    String driveCode = user.getEmployee().getStructure().getSeparatorCode();
	    List<Drive> drives = driveRepository.findByUploader_Structure_SeparatorCodeAndSeparatorCodeAndVisibleYn(separatorCode, driveCode, "Y");

	    long totalFiles = 0;
	    long totalDownloads = 0;
	    long totalSizeKB = 0;
	    LocalDateTime recentDate = null;

	    Map<String, Long> fileTypeSizeMap = new HashMap<>();
	    fileTypeSizeMap.put("pdf", 0L);
	    fileTypeSizeMap.put("image", 0L);
	    fileTypeSizeMap.put("docx", 0L);
	    fileTypeSizeMap.put("xlsx", 0L);
	    fileTypeSizeMap.put("zip", 0L);
	    fileTypeSizeMap.put("etc", 0L);

	    for (Drive drive : drives) {
	        totalFiles++;
	        totalDownloads += drive.getDownloadCount();
	        totalSizeKB += drive.getDriveSize();

	        String type = drive.getDriveType();
	        if (type == null) type = "";
	        type = type.startsWith(".") ? type.toLowerCase() : "." + type.toLowerCase();

	        switch (type) {
	            case ".pdf" -> fileTypeSizeMap.put("pdf", fileTypeSizeMap.get("pdf") + drive.getDriveSize());
	            case ".jpg", ".png" -> fileTypeSizeMap.put("image", fileTypeSizeMap.get("image") + drive.getDriveSize());
	            case ".docx" -> fileTypeSizeMap.put("docx", fileTypeSizeMap.get("docx") + drive.getDriveSize());
	            case ".xlsx" -> fileTypeSizeMap.put("xlsx", fileTypeSizeMap.get("xlsx") + drive.getDriveSize());
	            case ".zip" -> fileTypeSizeMap.put("zip", fileTypeSizeMap.get("zip") + drive.getDriveSize());
	            default -> fileTypeSizeMap.put("etc", fileTypeSizeMap.get("etc") + drive.getDriveSize());
	        }

	        if (recentDate == null || drive.getDriveRegDate().isAfter(recentDate)) {
	            recentDate = drive.getDriveRegDate();
	        }
	    }

	    Map<String, Double> fileTypeUsage = new HashMap<>();
	    for (Map.Entry<String, Long> entry : fileTypeSizeMap.entrySet()) {
	        fileTypeUsage.put(entry.getKey(), entry.getValue() * 1.0); // KB 유지
	    }

	    Map<String, Object> result = new HashMap<>();
	    result.put("totalFiles", totalFiles);
	    result.put("totalDownloads", totalDownloads);
	    result.put("recentUploadDate", recentDate != null ? recentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "-");
	    result.put("totalUsedKB", totalSizeKB);
	    result.put("fileTypeUsage", fileTypeUsage);

	    return result;
	}
	@GetMapping("/summary/company")
	@ResponseBody
	public Map<String, Object> getCompanyDriveSummary(@AuthenticationPrincipal EmployeeDetails user) {
	    Long employeeNo = user.getEmployee().getEmployeeNo();
	    String separatorCode = "A001";

	    List<Drive> drives = driveRepository.findBySeparatorCodeAndVisibleYn("A001", "Y");

	    long totalFiles = 0;
	    long totalDownloads = 0;
	    long totalSizeKB = 0;
	    LocalDateTime recentDate = null;

	    // 파일 타입별 크기 (KB단위로 모은다)
	    Map<String, Long> fileTypeSizeMap = new HashMap<>();
	    fileTypeSizeMap.put("pdf", 0L);
	    fileTypeSizeMap.put("image", 0L); // jpg, png
	    fileTypeSizeMap.put("docx", 0L);
	    fileTypeSizeMap.put("xlsx", 0L);
	    fileTypeSizeMap.put("zip", 0L);
	    fileTypeSizeMap.put("etc", 0L);

	    for (Drive drive : drives) {
	        totalFiles++;
	        totalDownloads += drive.getDownloadCount();
	        totalSizeKB += drive.getDriveSize(); // KB 단위 저장

	        String type = drive.getDriveType();
	        if (type == null) type = "";
	        type = type.startsWith(".") ? type.toLowerCase() : "." + type.toLowerCase();
	        
	        switch (type.toLowerCase()) {
	            case ".pdf":
	                fileTypeSizeMap.put("pdf", fileTypeSizeMap.get("pdf") + drive.getDriveSize());
	                break;
	            case ".jpg":
	            case ".png":
	                fileTypeSizeMap.put("image", fileTypeSizeMap.get("image") + drive.getDriveSize());
	                break;
	            case ".docx":
	                fileTypeSizeMap.put("docx", fileTypeSizeMap.get("docx") + drive.getDriveSize());
	                break;
	            case ".xlsx":
	                fileTypeSizeMap.put("xlsx", fileTypeSizeMap.get("xlsx") + drive.getDriveSize());
	                break;
	            case ".zip":
	                fileTypeSizeMap.put("zip", fileTypeSizeMap.get("zip") + drive.getDriveSize());
	                break;
	            default:
	                fileTypeSizeMap.put("etc", fileTypeSizeMap.get("etc") + drive.getDriveSize());
	        }
	        if (recentDate == null || drive.getDriveRegDate().isAfter(recentDate)) {
	            recentDate = drive.getDriveRegDate();
	        }
	    }

	    double totalUsedGB = totalSizeKB / 1024.0 / 1024.0; // KB → MB → GB

	    // 파일 타입별 GB 변환
	    Map<String, Double> fileTypeUsage = new HashMap<>();
	    for (Map.Entry<String, Long> entry : fileTypeSizeMap.entrySet()) {
	        fileTypeUsage.put(entry.getKey(), entry.getValue() / 1024.0 / 1024.0); // KB → GB
	    }

	    Map<String, Object> result = new HashMap<>();
	    result.put("totalFiles", totalFiles);
	    result.put("totalDownloads", totalDownloads);
	    result.put("recentUploadDate", recentDate != null ? recentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "-");
	    result.put("totalUsedGB", totalUsedGB);
	    result.put("fileTypeUsage", fileTypeUsage); 
	    result.put("totalUsedKB", totalSizeKB);
	    return result;
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
