package com.eroom.drive.service;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.directory.entity.Directory;
import com.eroom.directory.repository.DirectoryRepository;
import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Separator;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.employee.repository.SeparatorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
	
	private final DriveRepository driveRepository;
	private final SeparatorRepository separatorRepository;
	private final DirectoryRepository directoryRepository;
	private final EmployeeRepository employeeRepository;
	
	// 파일 저장 경로 
	 @Value("${ffupload.location}")
	 private String fileDir;
	 
	 public int uploadProfileImage(DriveDto driverDto, Long employeeNo) {
		 int result = 0;
		 
		 if(driverDto.getSeparatorCode() == null || driverDto.getSeparatorCode().isEmpty()) {
			 driverDto.setSeparatorCode(
					 separatorRepository.findBySeparatorName("프로필파일")
					 .map(s -> s.getSeparatorCode())
					 .orElse("FL008")
			);
		 }
		 
		 try {
			 // 업로드 한 사원 정보 
			 Employee employee = employeeRepository.findById(employeeNo).orElseThrow(() ->
					 new IllegalArgumentException("해당 사원이 존재하지 않습니다. employeeNo:" + employeeNo));
			 
			 // 업로트할 파일 
			 MultipartFile file = (driverDto.getDriveFiles() != null && !driverDto.getDriveFiles().isEmpty()) ? 
					 driverDto.getDriveFiles().get(0) : null;
			 if(file == null || file.isEmpty()) {
				 throw new IllegalArgumentException("업로드할 파일이 없습니다.");
			 }
			 
			 String oriName = file.getOriginalFilename();
			 String ext = oriName.substring(oriName.lastIndexOf("."));
			 String newName = UUID.randomUUID().toString().replace("-", "") + ext;
			 // 업로드 경로 
			 String path = fileDir + "profile/" + newName;
			 // 파일 저장
			 File savedFile = new File(path);
			 if(!savedFile.getParentFile().exists()) {
				 savedFile.getParentFile().mkdirs();
			 }
			 file.transferTo(savedFile);
			 
			 // Drive 엔티티 생성 후 저장
			 Drive drive = Drive.builder()
					 .uploader(Employee.builder().employeeNo(employeeNo).build())
					 .separatorCode(driverDto.getSeparatorCode())
					 .driveOriName(oriName)
					 .driveNewName(newName)
					 .driveType(ext)
					 .driveSize(file.getSize())
					 .drivePath("profile/"+newName)
					 .downloadCount(0L)
					 .visibleYn("Y")
					 .param1(driverDto.getParam1())
					 .build();
			 driveRepository.save(drive);
			 
			 // Directory 테이블 drive_attach_no 업데이트
			 Directory directory = directoryRepository.findByEmployee_EmployeeNo(employeeNo);
			 if(directory != null) {
				 directory.setDriveAttachNo(drive.getDriveAttachNo());
				 directoryRepository.save(directory);
			 }
			 result = 1;
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		
		 return result;
	 }
}
