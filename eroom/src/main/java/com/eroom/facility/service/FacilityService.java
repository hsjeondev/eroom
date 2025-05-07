package com.eroom.facility.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.facility.dto.FacilityDto;
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
	
	//회의실 목록 조회
	public List<Facility>selectMeetingRoomAll(){
		return repository.findBySeparatorCode("F001");
	}
	// 회의실 정보 조회
	public FacilityDto selectFacilityByNo(Long facilityNo) {
		Facility facility = repository.findById(facilityNo).orElse(null);
		if(facility == null) return null;
		return new FacilityDto().toDto(facility);
	}
	// 회의실 이름 중복 체크
    public boolean existsByNameIgnoringSpaces(String name) {
        if (name == null) return false;

        String trimmedInput = name.replaceAll("\\s+", "").toLowerCase();

        List<Facility> allFacilities = repository.findAll();

        for (Facility facility : allFacilities) {
            String facilityName = facility.getFacilityName();
            if (facilityName == null) continue;

            String trimmedFacilityName = facilityName.replaceAll("\\s+", "").toLowerCase();
            if (trimmedInput.equals(trimmedFacilityName)) {
                return true;
            }
        }

        return false;
    }	
    // 회의실 생성
    public void createFacility(FacilityDto dto) {
        Facility facility = dto.toEntity();
        repository.save(facility);
    }    
}
