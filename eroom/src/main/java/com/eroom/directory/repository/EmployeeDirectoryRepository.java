package com.eroom.directory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.directory.entity.Directory;
import com.eroom.employee.entity.Separator;

public interface EmployeeDirectoryRepository extends JpaRepository<Directory, Long>{

	List<Directory> findBySeparator_SeparatorCode(String separatorCode);

}
