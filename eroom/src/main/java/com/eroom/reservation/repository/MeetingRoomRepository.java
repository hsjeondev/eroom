package com.eroom.reservation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.reservation.entity.MeetingRoom;
import com.eroom.reservation.entity.Vehicle;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom,Long>,JpaSpecificationExecutor<MeetingRoom> {
	@Query("SELECT m FROM MeetingRoom m WHERE m.facilityNo = :facilityNo AND m.reservationStart < :endOfDay AND m.reservationEnd > :startOfDay")
	List<MeetingRoom> findByFacilityNoAndReservationDate(
	    @Param("facilityNo") Long facilityNo,
	    @Param("startOfDay") LocalDateTime startOfDay,
	    @Param("endOfDay") LocalDateTime endOfDay
	);
	
	List<MeetingRoom> findBySeparatorCodeAndVisibleYn(String separatorCode, String visibleYn);

}
