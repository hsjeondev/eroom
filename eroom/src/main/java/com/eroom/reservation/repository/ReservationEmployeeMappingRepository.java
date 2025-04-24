package com.eroom.reservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.reservation.entity.MeetingRoom;
import com.eroom.reservation.entity.ReservationEmployeeMapping;

public interface ReservationEmployeeMappingRepository extends JpaRepository<ReservationEmployeeMapping,Long> {
	  List<ReservationEmployeeMapping> findByReservation(MeetingRoom reservation);
}
