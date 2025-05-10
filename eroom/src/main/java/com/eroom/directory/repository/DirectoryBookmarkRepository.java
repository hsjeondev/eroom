package com.eroom.directory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.directory.entity.DirectoryBookmark;

public interface DirectoryBookmarkRepository extends JpaRepository<DirectoryBookmark, Long> {

	List<DirectoryBookmark> findByEmployee_employeeNoAndDirectory_Employee_employeeNoAndVisibleYn(Long employeeNo,
			Long id, String string);
	
	
}
