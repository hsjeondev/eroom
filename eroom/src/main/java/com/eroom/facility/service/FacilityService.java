package com.eroom.facility.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.facility.entity.Facility;
import com.eroom.facility.repository.FacilityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacilityService {
	private final FacilityRepository repository;
	
	//차량 목록 조회
	public List<Facility>selectVehicleAll(){
		return repository.findBySeparatorCode("F002");
	}
}
