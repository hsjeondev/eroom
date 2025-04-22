package com.eroom.reservation.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.eroom.reservation.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle,Long>,JpaSpecificationExecutor<Vehicle> {
	List<Vehicle> findBySeparatorCodeAndVisibleYn(String separatorCode, String visibleYn);
	
}
