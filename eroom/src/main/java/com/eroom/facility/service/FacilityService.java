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
	
	// 회의실 목록(visible_yn = 'Y') 조회
	public List<Facility> selectVisibleMeetingRooms() {
	    return repository.findBySeparatorCodeAndVisibleYn("F001", "Y");
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
    public void createMeetingroom(FacilityDto dto) {
        Facility facility = dto.toEntity();
        repository.save(facility);
    }
    
    // 회의실 수정
    public Facility updateMeetingroom(FacilityDto dto) {
        Facility facility = repository.findById(dto.getFacility_no()).orElse(null);
        if (facility == null) return null;
        // 이름이 존재하고 공백이 아닌 경우만 반영
        if (dto.getFacility_name() != null && !dto.getFacility_name().trim().isEmpty()) {
            facility.setFacilityName(dto.getFacility_name().trim());
        }
        // 수용 인원이 존재하고 숫자로 해석 가능한 경우만 반영
        if (dto.getFacility_capacity() != null && dto.getFacility_capacity().matches("\\d+")) {
            facility.setFacilityCapacity(dto.getFacility_capacity().trim());
        }
        // 수정자와 업데이트
        facility.setFacilityEditor(dto.getFacility_editor());
        return repository.save(facility);
    }
    
    // 회의실 삭제
    public Facility deleteMeetingroom(FacilityDto dto) {
        Facility facility = repository.findById(dto.getFacility_no()).orElse(null);
        if (facility == null) return null;
        // 삭제 처리: visible_yn = 'N'으로 변경
        facility.setVisibleYn("N");
        // 수정자
        facility.setFacilityEditor(dto.getFacility_editor());
        return repository.save(facility);
    }
}
