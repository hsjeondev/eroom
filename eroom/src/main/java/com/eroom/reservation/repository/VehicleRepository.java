package com.eroom.reservation.repository;



import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.reservation.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle,Long>,JpaSpecificationExecutor<Vehicle> {
	List<Vehicle> findBySeparatorCodeAndVisibleYn(String separatorCode, String visibleYn);
	
	 @Query("SELECT v FROM Vehicle v WHERE v.facilityNo = :facilityNo AND FUNCTION('DATE', v.reservationStart) = :date")
	    List<Vehicle> findByFacilityNoAndReservationDate(@Param("facilityNo") String facilityNo,
	                                                     @Param("date") LocalDate date);
	
}
