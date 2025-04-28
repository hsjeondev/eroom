package com.eroom.drive.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.drive.entity.Drive;

@Repository
public interface DriveRepository extends JpaRepository<Drive, Long>{
	// 개인 드라이브 파일 리스트 조회
	List<Drive> findByUploader_EmployeeNoAndVisibleYn(Long employeeNo, String visibleYn);
	// 개인 드라이브 파일 상세 조회
	Drive findByDriveAttachNo(Long driveAttachNo);
	// 개인 드라이브 파일 삭제
	@Modifying
	@Query("UPDATE Drive df SET df.visibleYn = 'N' WHERE df.driveAttachNo = :attachNo")
	int updateDeleteStatus(@Param("attachNo") Long attachNo);

    // 개인 파일 일괄 삭제
	@Modifying
    @Query("UPDATE Drive df SET df.visibleYn = 'N' WHERE df.driveAttachNo IN :attachNos")
    int updateBulkDeleteStatus(@Param("attachNos") List<Long> attachNos);
	
	// 파일 다운로드 카운트 증가
	@Modifying
	@Transactional
	@Query("UPDATE Drive d SET d.downloadCount = d.downloadCount + 1 WHERE d.driveAttachNo = :id")
	void updateDownloadCount(@Param("id") Long id);
	
	
	
	// ------- 결재 관련해서 생성한 JPA
	// param1과 Drive_delete_yn N을 기준으로 조회
	List<Drive> findByParam1AndVisibleYnAndSeparatorCode(Long param1, String visibleYn, String separatorCode);
	// driveAttachNo와 Drive_delete_yn N을 기준으로 조회
	Drive findByDriveAttachNoAndVisibleYnAndSeparatorCode(Long driveAttachNo, String visibleYn, String separatorCode);

	// 메일 파일 조회
	//List<Drive> findBySeparatorCodeAndParam1AndDriveDeleteYn(String separatorCode, Long param1, String driveDeleteYn);
	List<Drive> findBySeparatorCodeAndParam1AndVisibleYn(String separatorCode, Long param1, String visibleYn);
}
