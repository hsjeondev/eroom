package com.eroom.drive.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.SeparatorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class DriveService {
	
	private final DriveRepository driveRepository;
	private final SeparatorRepository separatorRepository;
	
	// 파일 저장 경로 
	 @Value("${ffupload.location}")
	 private String fileDir;
	
	// ------------------------- 개인 드라이브 파일 업로드 --------------------------
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

		        String path = fileDir + "drive/personal/" + newName;
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
		                .drivePath("drive/personal/" + newName)
		                .driveDescription(description)
		                .downloadCount(0L)
		                .visibleYn("Y")
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
	// ------------------------- 개인 드라이브 파일 리스트 조회 --------------------------
	public List<DriveDto> findPersonalDriveFiles(Long employeeNo) {
	    List<Drive> drives = driveRepository.findByUploader_EmployeeNoAndVisibleYn(employeeNo, "Y");
	    List<DriveDto> result = new ArrayList<>();

	    for (Drive drive : drives) {
	        result.add(DriveDto.toDto(drive));
	    }

	    return result;
	}
	// ------------------------- 개인 드라이브 파일 수정 --------------------------
	public boolean updateDriveFile(Long attachNo, MultipartFile file, String description) {
	    try {
	        Optional<Drive> optionalDrive = driveRepository.findById(attachNo);
	        if (optionalDrive.isEmpty()) return false;

	        Drive drive = optionalDrive.get();

	        // 파일이 있으면 교체
	        if (file != null && !file.isEmpty()) {
	            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
	            String newName = UUID.randomUUID().toString().replace("-", "") + ext;
	            String path = fileDir + "drive/personal/" + newName;
	            File newFile = new File(path);
	            if (!newFile.getParentFile().exists()) newFile.getParentFile().mkdirs();
	            file.transferTo(newFile);
	            // 기존 정보 업데이트
	            drive.setDriveOriName(file.getOriginalFilename());
	            drive.setDriveNewName(newName);
	            drive.setDriveType(ext);
	            drive.setDriveSize(file.getSize());
	            drive.setDrivePath("drive/personal/" + newName);
	        }
	        // 설명만 변경할 수도 있음
	        drive.setDriveDescription(description);
	        drive.setDriveModDate(LocalDateTime.now());

	        driveRepository.save(drive);
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	// ------------------------- 개인 드라이브 파일 삭제 --------------------------
	@Transactional
	public int deleteDriveFile(Long driveAttachNo) {
	   return driveRepository.updateDeleteStatus(driveAttachNo);
	}
	// ------------------------- 개인 드라이브 파일 다중 삭제 --------------------------
	@Transactional
    public int bulkDeleteDriveFiles(List<Long> fileIds) {
       return driveRepository.updateBulkDeleteStatus(fileIds);  // 파일 상태 일괄 업데이트
    }
	// ------------------------- 개인 드라이브 파일 다운로드 --------------------------
	@Transactional
	public Drive findByDriveAttachNo(Long id) {
	    Drive drive = driveRepository.findByDriveAttachNo(id);
	    if (drive != null) {
	        driveRepository.updateDownloadCount(id); // downloadCount만 올림
	    } else {
	        System.out.println("해당 ID로 파일을 찾을 수 없음");
	    }
	    return drive;
	}

	
	// ------------------------- 결재 파일 업로드 --------------------------
	public int uploadApprovalAttachFiles(DriveDto driverDto, Long employeeNo) {
		int result = 0;
		
		 if (driverDto.getSeparatorCode() == null || driverDto.getSeparatorCode().isEmpty()) {
	            driverDto.setSeparatorCode(
	                separatorRepository.findBySeparatorName("결재파일")
	                    .map(s -> s.getSeparatorCode())
	                    .orElse("FL007") 
	            );
	        }
		
		List<MultipartFile> files = driverDto.getDriveFiles();

		for (int i = 0; i < files.size(); i++) {
		    MultipartFile file = files.get(i);
		    try {
//		        System.out.println("파일 처리 시작: " + file.getOriginalFilename());

		        String oriName = file.getOriginalFilename();
		        String ext = oriName.substring(oriName.lastIndexOf("."));
		        String newName = UUID.randomUUID().toString().replace("-", "") + ext;

		        String path = fileDir + "personal/" + newName;
		        File savedFile = new File(path);
		        if (!savedFile.getParentFile().exists()) {
		            savedFile.getParentFile().mkdirs();
		        }
		        file.transferTo(savedFile);


		        Drive drive = Drive.builder()
		                .uploader(Employee.builder().employeeNo(employeeNo).build())
		                .separatorCode(driverDto.getSeparatorCode())
		                .driveOriName(oriName)
		                .driveNewName(newName)
		                .driveType(ext)
		                .driveSize(file.getSize())
		                .drivePath(path)
		                .downloadCount(0L)
		                .visibleYn("Y")
		                .param1(driverDto.getParam1())
		                .build();
		        
		        driveRepository.save(drive);
		        result = 1;
		    } catch (Exception e) {
		        System.out.println("업로드 실패 파일명: " + file.getOriginalFilename());
		        e.printStackTrace();
		    }
		}
		return result;
	}
	// ------------------------- 결재 파일 리스트 조회 --------------------------
	// 결재 번호(param1)로 조회
	public List<DriveDto> findApprovalDriveFiles(Long param1) {
	    List<Drive> drives = driveRepository.findByParam1AndVisibleYnAndSeparatorCode(param1, "N", "Fl007");
	    List<DriveDto> result = new ArrayList<>();

	    for (Drive drive : drives) {
	        result.add(DriveDto.toDto(drive));
	    }

	    return result;
	}
	// drive 번호(param1)로 조회 및 approvalNo 주입 후 새로운 Entity로 save
	public int createNewFilesUseOldFiles(List<Long> approvalAttachFileIds, Long approvalNo) {
		int result = 0;
		try {
			for(Long l : approvalAttachFileIds) {
				Drive oldDrive = driveRepository.findByDriveAttachNoAndVisibleYnAndSeparatorCode(l, "N", "FL007");
				// 영속성 없는 객체 생성
	            Drive newDrive = new Drive();
	            
	            newDrive.setUploader(oldDrive.getUploader());
	            newDrive.setSeparatorCode(oldDrive.getSeparatorCode());
	            newDrive.setDriveOriName(oldDrive.getDriveOriName());
	            newDrive.setDriveNewName(oldDrive.getDriveNewName());
	            newDrive.setDriveType(oldDrive.getDriveType());
	            newDrive.setDriveSize(oldDrive.getDriveSize());
	            newDrive.setDrivePath(oldDrive.getDrivePath());
	            newDrive.setDriveDescription(oldDrive.getDriveDescription());
	            newDrive.setDownloadCount(0L);
	            newDrive.setVisibleYn("Y");
	            newDrive.setParam1(approvalNo);

	            driveRepository.save(newDrive);
			}
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	// ------------------------- 결재 파일 삭제 --------------------------
	// bulkDeleteDriveFiles() 사용

}
