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
	
	
	@Query("SELECT COUNT(m) > 0 FROM MeetingRoom m " +
		       "WHERE m.facilityNo = :facilityNo " +
		       "AND m.visibleYn = 'Y' " +
		       "AND m.reservationStart < :end " +
		       "AND m.reservationEnd > :start")
		boolean existsConflict(
		    @Param("facilityNo") Long facilityNo,
		    @Param("start") LocalDateTime start,
		    @Param("end") LocalDateTime end
		);
	

	@Query("SELECT r FROM MeetingRoom r WHERE r.visibleYn = 'Y' AND r.separatorCode = 'F001' AND DATE(r.reservationStart) = CURRENT_DATE")
	List<MeetingRoom> findTodayMeetingRoomReservations();
}
