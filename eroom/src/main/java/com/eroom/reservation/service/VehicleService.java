package com.eroom.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.facility.entity.Facility;
import com.eroom.reservation.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {
	
	private final VehicleRepository repository;
	
	public List<Facility>selectVehicleAll(){
		return repository.findBySeparatorCode("F002");
	}
}
