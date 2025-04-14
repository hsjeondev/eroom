package com.eroom.directory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.directory.entity.Directory;

public interface EmployeeDirectoryRepository extends JpaRepository<Directory, Long>{

}
