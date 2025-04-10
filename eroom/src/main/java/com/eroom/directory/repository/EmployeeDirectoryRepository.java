package com.eroom.directory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.directory.entity.EmployeeDirectory;

public interface EmployeeDirectoryRepository extends JpaRepository<EmployeeDirectory, Long>{

}
