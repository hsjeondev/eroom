package com.eroom.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.employee.entity.Separator;

public interface SeparatorRepository extends JpaRepository<Separator, String> {
    Optional<Separator> findBySeparatorName(String name); 
    Optional<Separator> findBySeparatorCode(String code);
    // parentCode, visible Y 조회
	List<Separator> findBySeparatorParentCodeAndVisibleYn(String deleteDeptCode, String string); 
}
