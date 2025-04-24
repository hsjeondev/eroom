package com.eroom.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.reservation.entity.ReservationEmployeeMapping;

public interface ReservationEmployeeMappingRepository extends JpaRepository<ReservationEmployeeMapping,Long> {

}
