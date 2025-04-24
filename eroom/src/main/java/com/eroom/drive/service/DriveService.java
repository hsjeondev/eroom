package com.eroom.drive.service;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.SeparatorRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class DriveService {
	
	private final DriveRepository driveRepository;
	private final SeparatorRepository separatorRepository;
	 @Value("${ffupload.location}")
	 private String fileDir;
	
	public int uploadDriveFiles(DriveDto driverDto, Long employeeNo) {
		int result = 0;
		
		 if (driverDto.getSeparatorCode() == null || driverDto.getSeparatorCode().isEmpty()) {
	            driverDto.setSeparatorCode(
	                separatorRepository.findBySeparatorName("개인")
	                    .map(s -> s.getSeparatorCode())
	                    .orElse("E001")
	            );
	        }
		
		 for (MultipartFile file : driverDto.getDriveFiles()) {
			    try {
			    	System.out.println("파일 처리 시작: " + file.getOriginalFilename());
			        // 1. 원본 파일명, 확장자
			        String oriName = file.getOriginalFilename();
			        String ext = oriName.substring(oriName.lastIndexOf("."));

			        // 2. 새로운 파일명 생성
			        String newName = UUID.randomUUID().toString().replace("-", "") + ext;

			        // 3. 파일 저장 경로 먼저 정의해야 함
			        String path = fileDir + "personal/" + newName;
			        File savedFile = new File(path);

			        // 4. 폴더가 없으면 자동 생성
			        if (!savedFile.getParentFile().exists()) {
			            savedFile.getParentFile().mkdirs();
			        }

			        // 5. 파일 저장
			        file.transferTo(savedFile);

			        // 6. Drive 엔티티 생성 및 저장
			        Drive drive = Drive.builder()
			                .uploader(Employee.builder().employeeNo(employeeNo).build())
			                .separatorCode(driverDto.getSeparatorCode())
			                .driveOriName(oriName)
			                .driveNewName(newName)
			                .driveType(ext)
			                .driveSize(file.getSize())
			                .drivePath("/upload/personal/" + newName)
			                .downloadCount(0L)
			                .driveDeleteYn("N")
			                .build();

			        driveRepository.save(drive);
			        result++;

			    } catch (Exception e) {
			    	System.out.println("업로드 실패 파일명: " + file.getOriginalFilename());
			        e.printStackTrace();
			    }
		}
		return result;
	}

}
