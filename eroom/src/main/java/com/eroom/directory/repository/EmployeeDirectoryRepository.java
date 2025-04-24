package com.eroom.directory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.directory.entity.Directory;
import com.eroom.employee.entity.Separator;

public interface EmployeeDirectoryRepository extends JpaRepository<Directory, Long>{

	// separator_code로 주소록 리스트 조회(service 에서 separatorCode를 구분해서 사용)
	List<Directory> findBySeparator_SeparatorCode(String separatorCode);
	
	// 주소록 정보 조회
	Directory findByEmployee_EmployeeNo(Long employeeNo);

}
