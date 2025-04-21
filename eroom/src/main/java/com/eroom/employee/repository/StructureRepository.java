package com.eroom.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.employee.entity.Structure;

public interface StructureRepository extends JpaRepository<Structure, Long>{

	// 부서 하위의 모든 팀 조회
	List<Structure> findBySeparatorCodeStartingWith(String prefix);

		
	// 부모 코드를 매개변수로 Structure 객체를 조회
	Structure findBySeparatorCode(String separatorCode);

	// 모든 부서 조회
	List<Structure> findByParentCodeIsNull();

	// 부서코드를 통한 부서 하위의 모든 팀 조회
	List<Structure> findByParentCode(String parentCode);
	
	//부서만 조회
	@Query("SELECT s FROM Structure s WHERE s.parentCode IS NULL AND s.visibleYn = 'Y'")
	List<Structure> findOnlyDepartments();
	
	//부서 캘린더 조회
	@Query("SELECT s.separatorCode FROM Structure s WHERE s.parentCode = :departmentCode AND s.visibleYn = 'Y'")
	List<String> findTeamCodesByDepartment(@Param("departmentCode") String departmentCode);
}
