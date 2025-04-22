package com.eroom.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.facility.entity.Facility;
import com.eroom.reservation.dto.VehicleDto;
import com.eroom.reservation.entity.Vehicle;
import com.eroom.reservation.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {
	
	private final VehicleRepository repository;
	
	//예약 등록
	public VehicleDto vehicleReservation(VehicleDto dto) {
		Vehicle param = dto.toEntity();
		Vehicle result = repository.save(param);
		return new VehicleDto().toDto(result);
	}
}
