package com.eroom.directory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.directory.entity.Directory;
import com.eroom.employee.entity.Separator;

public interface DirectoryRepository extends JpaRepository<Directory, Long>{

	// separator_code로 주소록 리스트 조회(service 에서 separatorCode를 구분해서 사용)
	List<Directory> findBySeparator_SeparatorCode(String separatorCode);
	
	// 주소록 정보 조회
	Directory findByEmployee_EmployeeNo(Long employeeNo);

	@Query("SELECT d FROM Directory d JOIN FETCH d.separator WHERE d.separator.separatorCode = :separatorCode")
	List<Directory> findBySeparatorCode(@Param("separatorCode") String separatorCode);


}
