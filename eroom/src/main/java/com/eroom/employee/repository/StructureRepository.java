package com.eroom.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.employee.entity.Structure;

public interface StructureRepository extends JpaRepository<Structure, Long>{

	// 부서 하위의 모든 팀 조회
	@Query("SELECT s FROM Structure s WHERE s.separatorCode LIKE CONCAT(:prefix, '%') AND s.visibleYn = 'Y'")
	List<Structure> findVisibleBySeparatorCodeStartingWith(@Param("prefix") String prefix);
		
	// 부모 코드를 매개변수로 Structure 객체를 조회
	Structure findBySeparatorCode(String separatorCode);

	// 모든 부서 조회
	List<Structure> findByParentCodeIsNullOrderBySortOrderAsc();
	List<Structure> findByVisibleYnAndParentCodeIsNullOrderBySortOrderAsc(String visibleYn);

	// 부서코드를 통한 부서 하위의 모든 팀 조회
//	List<Structure> findByParentCode(String parentCode);
//	List<Structure> findByParentCodeOrderBySortOrderAsc(String parentCode);
	
	//부서만 조회
//	@Query("SELECT s FROM Structure s WHERE s.parentCode IS NULL AND s.visibleYn = 'Y'")
	@Query("SELECT s FROM Structure s WHERE s.parentCode IS NULL AND s.visibleYn = 'Y' ORDER BY s.sortOrder")
	List<Structure> findOnlyDepartments();
	// 팀만 조회 visible Y만
	@Query("SELECT s FROM Structure s WHERE s.parentCode IS NOT NULL AND s.visibleYn = 'Y' ORDER BY s.sortOrder")
	List<Structure> findOnlyTeamsVisibleY();
	// 팀만 조회(visible 상관없이!)
	@Query("SELECT s FROM Structure s WHERE s.parentCode IS NOT NULL ORDER BY s.sortOrder")
	List<Structure> findOnlyTeams();
	// 부서만 조회(visible 상관없이!)
	@Query("SELECT s FROM Structure s WHERE s.parentCode IS NULL ORDER BY s.sortOrder")
	List<Structure> findOnlyDepts();
	
	//부서 캘린더 조회
//	@Query("SELECT s.separatorCode FROM Structure s WHERE s.parentCode = :departmentCode AND s.visibleYn = 'Y'") 
	@Query("SELECT s.separatorCode FROM Structure s WHERE s.parentCode = :departmentCode AND s.visibleYn = 'Y' ORDER BY s.sortOrder")
	List<String> findTeamCodesByDepartment(@Param("departmentCode") String departmentCode);

	// 부서코드를 통한 부서 하위의 모든 팀 조회
//	List<Structure> findByParentCodeAndVisibleYn(String departmentSeparatorCode, String visibleYn);
	List<Structure> findByParentCode(String departmentSeparatorCode);
	List<Structure> findByParentCodeOrderBySortOrderAsc(String departmentSeparatorCode);
	List<Structure> findByParentCodeAndVisibleYnOrderBySortOrderAsc(String departmentSeparatorCode, String visibleYn);

	// 부서이름 중복 확인
	boolean existsByCodeNameAndVisibleYn(String codeName, String string);

	// 팀이름 중복 확인
	boolean existsByCodeNameAndParentCodeAndVisibleYn(String codeName, String parentCode, String string);

}
