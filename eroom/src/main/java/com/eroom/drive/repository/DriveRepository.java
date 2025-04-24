package com.eroom.drive.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eroom.drive.entity.Drive;

@Repository
public interface DriveRepository extends JpaRepository<Drive, Long>{
	// 개인 드라이브 파일 리스트 조회
	List<Drive> findByUploader_EmployeeNoAndDriveDeleteYn(Long employeeNo, String deleteYn);
	
}
