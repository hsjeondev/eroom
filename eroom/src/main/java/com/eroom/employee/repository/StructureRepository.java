package com.eroom.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.employee.entity.Structure;

public interface StructureRepository extends JpaRepository<Structure, Long>{

	// 부서 하위의 모든 팀 조회
	List<Structure> findBySeparatorCodeStartingWith(String prefix);
}
