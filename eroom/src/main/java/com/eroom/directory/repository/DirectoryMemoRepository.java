package com.eroom.directory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.directory.entity.DirectoryMemo;

public interface DirectoryMemoRepository extends JpaRepository<DirectoryMemo, Long> {

	List<DirectoryMemo> findByEmployee_employeeNoAndDirectory_Employee_employeeNoAndVisibleYn(Long employeeNo, Long id, String visibleYn);

}
