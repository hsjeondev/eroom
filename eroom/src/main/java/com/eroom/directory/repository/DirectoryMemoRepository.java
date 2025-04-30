package com.eroom.directory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.directory.entity.DirectoryMemo;

public interface DirectoryMemoRepository extends JpaRepository<DirectoryMemo, Long> {

	DirectoryMemo findByEmployee_employeeNoAndDirectory_Employee_employeeNo(Long employeeNo, Long id);

}
