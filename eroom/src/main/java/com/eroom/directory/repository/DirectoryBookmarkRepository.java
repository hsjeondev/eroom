package com.eroom.directory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.directory.entity.DirectoryBookmark;

public interface DirectoryBookmarkRepository extends JpaRepository<DirectoryBookmark, Long> {
	
	// 상대(directory의 employeeNo) 나(employeeNo) visiebleYn 기준으로 조회
	List<DirectoryBookmark> findByEmployee_employeeNoAndDirectory_Employee_employeeNoAndVisibleYn(Long employeeNo,
			Long id, String string);

	List<DirectoryBookmark> findByEmployee_employeeNoAndDirectory_Employee_employeeNoAndVisibleYnAndDirectoryBookmarkYn(
			Long employeeNo, Long id, String string, String string2);
	
	
}
