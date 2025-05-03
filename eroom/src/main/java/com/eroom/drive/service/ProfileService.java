package com.eroom.drive.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.directory.repository.DirectoryRepository;
import com.eroom.drive.repository.DriveRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
	
	private final DriveRepository driveRepository;
	private final DirectoryRepository directoryRepository;
	
	// 파일 저장 경로 
	 @Value("${ffupload.location}")
	 private String fileDir;
	 
	 public void updateProfileImage(MultipartFile file, Long employeeNo, boolean useDefault) {
		 
	 }
}
