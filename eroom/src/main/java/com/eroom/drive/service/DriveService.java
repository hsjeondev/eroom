package com.eroom.drive.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
		
		List<String> descriptions = driverDto.getDriveDescriptions(); // 추가된 설명 리스트 가져오기
		List<MultipartFile> files = driverDto.getDriveFiles();

		for (int i = 0; i < files.size(); i++) {
		    MultipartFile file = files.get(i);
		    try {
		        System.out.println("파일 처리 시작: " + file.getOriginalFilename());

		        String oriName = file.getOriginalFilename();
		        String ext = oriName.substring(oriName.lastIndexOf("."));
		        String newName = UUID.randomUUID().toString().replace("-", "") + ext;

		        String path = fileDir + "personal/" + newName;
		        File savedFile = new File(path);
		        if (!savedFile.getParentFile().exists()) {
		            savedFile.getParentFile().mkdirs();
		        }
		        file.transferTo(savedFile);

		        // 파일 설명 추가
		        String description = (descriptions != null && descriptions.size() > i) ? descriptions.get(i) : null;

		        Drive drive = Drive.builder()
		                .uploader(Employee.builder().employeeNo(employeeNo).build())
		                .separatorCode(driverDto.getSeparatorCode())
		                .driveOriName(oriName)
		                .driveNewName(newName)
		                .driveType(ext)
		                .driveSize(file.getSize())
		                .drivePath("/upload/personal/" + newName)
		                .driveDescription(description)
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

	public List<DriveDto> findPersonalDriveFiles(Long employeeNo) {
	    List<Drive> drives = driveRepository.findByUploader_EmployeeNoAndDriveDeleteYn(employeeNo, "N");
	    List<DriveDto> result = new ArrayList<>();

	    for (Drive drive : drives) {
	        result.add(DriveDto.toDto(drive));
	    }

	    return result;
	}


}
