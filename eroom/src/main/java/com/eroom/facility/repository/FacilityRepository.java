package com.eroom.facility.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.facility.entity.Facility;

public interface FacilityRepository extends JpaRepository<Facility,Long> {
	List<Facility> findBySeparatorCode(String separatorCode);

}
