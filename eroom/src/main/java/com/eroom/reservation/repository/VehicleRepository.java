package com.eroom.reservation.repository;



import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.reservation.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle,Long>,JpaSpecificationExecutor<Vehicle> {
	List<Vehicle> findBySeparatorCodeAndVisibleYn(String separatorCode, String visibleYn);
	
	@Query("SELECT v FROM Vehicle v WHERE v.facilityNo = :facilityNo AND v.reservationStart < :endOfDay AND v.reservationEnd > :startOfDay")
	List<Vehicle> findByFacilityNoAndReservationDate(
	    @Param("facilityNo") Long facilityNo,
	    @Param("startOfDay") LocalDateTime startOfDay,
	    @Param("endOfDay") LocalDateTime endOfDay
	);
	 
	 List<Vehicle> findByFacilityNo(Long facilityNo);
	 
	 @Query("SELECT COUNT(v) > 0 FROM Vehicle v " +
		       "WHERE v.facilityNo = :facilityNo " +
		       "AND v.visibleYn = 'Y' " +
		       "AND v.reservationStart < :end " +
		       "AND v.reservationEnd > :start")
		boolean existsConflict(
		    @Param("facilityNo") Long facilityNo,
		    @Param("start") LocalDateTime start,
		    @Param("end") LocalDateTime end
		);
	
	 @Query("SELECT v FROM Vehicle v WHERE DATE(v.reservationStart) = CURRENT_DATE AND v.visibleYn = 'Y' AND v.separatorCode = 'F002'")
	 List<Vehicle> findTodayVehicleReservations();
	 
	 List<Vehicle> findByVisibleYnAndSeparatorCodeOrderByReservationStartDesc(String visibleYn, String separatorCode);
}
