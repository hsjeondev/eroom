package com.eroom.facility.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.facility.entity.Facility;

public interface FacilityRepository extends JpaRepository<Facility,Long> {
	//separatorCode를 기준으로 목록조회
	List<Facility> findBySeparatorCode(String separatorCode);
	
	//차량 이름을 가져옴
	Facility findByFacilityNo(Long long1);
	
	// 삭제되지 않은 목록 조회
	List<Facility> findBySeparatorCodeAndVisibleYn(String separatorCode, String visibleYn);

	
}
