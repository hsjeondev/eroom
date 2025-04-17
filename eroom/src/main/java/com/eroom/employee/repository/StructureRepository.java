package com.eroom.employee.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
