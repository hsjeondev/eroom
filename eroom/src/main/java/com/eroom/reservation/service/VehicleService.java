package com.eroom.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.facility.entity.Facility;
import com.eroom.facility.repository.FacilityRepository;
import com.eroom.reservation.dto.VehicleDto;
import com.eroom.reservation.entity.Vehicle;
import com.eroom.reservation.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {
	
	private final VehicleRepository repository;
	private final FacilityRepository facilityRepositroy;
	private final EmployeeRepository employeeRepository;
	
	//예약 등록
	public VehicleDto vehicleReservation(VehicleDto dto) {
		Vehicle param = dto.toEntity();
		Vehicle result = repository.save(param);
		return new VehicleDto().toDto(result);
	}
	
	public List<VehicleDto> getVehicleList(String separator){
	    List<Vehicle> list = repository.findBySeparatorCodeAndVisibleYn(separator,"Y");
	    List<VehicleDto> dtoList = new ArrayList<>();

	    for (Vehicle vehicle : list) {
	        // 먼저 toDto() 호출해서 기본값 세팅
	        VehicleDto dto = new VehicleDto().toDto(vehicle);

	        // facility_name 가져오기
	        Facility facility = facilityRepositroy.findByFacilityNo(vehicle.getFacilityNo());
	        if (facility != null) {
	            dto.setVehicleName(facility.getFacilityName()); // 차량 이름
	        }

	        // employee_name 가져오기
	        Employee employee = employeeRepository.findByEmployeeNo(vehicle.getEmployeeNo());
	        if (employee != null) {
	            dto.setReserverName(employee.getEmployeeName()); // 예약자 이름
	        }

	        dtoList.add(dto); //
	    }

	    return dtoList;
	}
	
	//해당 예약시간 select창에서 막기
//	public List<String> getBookedTimes(String date, String facilityNoStr) {
//	    LocalDate targetDate = LocalDate.parse(date);
//	    Long facilityNo = Long.parseLong(facilityNoStr);
//
//	    // 오늘 날짜의 09:00 ~ 23:59 범위
//	    LocalDateTime dayStart = targetDate.atTime(9, 0);
//	    LocalDateTime dayEnd = targetDate.atTime(23, 59);
//
//	    // 예약 리스트 조회 (전날 ~ 다음날까지 겹치는 예약 모두 포함)
//	    List<Vehicle> reservedList = repository.findByFacilityNoAndReservationDate(
//	        facilityNo,
//	        dayStart.minusDays(1),
//	        dayEnd.plusDays(1)
//	    );
//
//	    Set<String> bookedTimesSet = new HashSet<>();
//
//	    for (Vehicle v : reservedList) {
//	        LocalDateTime resStart = v.getReservationStart();
//	        LocalDateTime resEnd = v.getReservationEnd();
//
//	        // 조회일과 겹치는 구간만 필터링
//	        if (resEnd.isBefore(dayStart) || resStart.isAfter(dayEnd)) continue;
//
//	        // 예약 시작/종료가 현재 날짜 기준인지 확인
//	        boolean startsToday = resStart.toLocalDate().isEqual(targetDate);
//	        boolean endsToday = resEnd.toLocalDate().isEqual(targetDate);
//
//	        int startHour = startsToday ? Math.max(resStart.getHour(), 9) : 9;
//	        int endHour = endsToday 
//	            ? (resEnd.getMinute() > 0 ? resEnd.getHour() + 1 : resEnd.getHour()) 
//	            : 23;
//
//	        for (int hour = startHour; hour <= endHour; hour++) {
//	            if (hour >= 9 && hour <= 23) {
//	                bookedTimesSet.add(String.format("%02d:00", hour));
//	            }
//	        }
//	    }
//
//	    List<String> result = new ArrayList<>(bookedTimesSet);
//	    Collections.sort(result);
//	    return result;
//	}

	
	//해당 예약 단일 조회
	public VehicleDto findByReservationNo(Long id) {
		Vehicle vehicle = repository.findById(id).orElse(null);
		
		if(vehicle == null) {
			return null;
		}
		
		VehicleDto dto = new VehicleDto();
		
		return dto.toDto(vehicle);
	}
	
	//해당 예약 수정
	public Vehicle updateVehicle(VehicleDto param) {
		Vehicle result = null;
		Vehicle target = repository.findById(param.getReservation_no()).orElse(null);
		
		if(target != null) {
			param.setReservation_creator(target.getReservationCreator());
			result = repository.save(param.toEntity());
		}
		return result;
	}
	
	//해당 예약 삭제
	public VehicleDto deleteCalendar(Long id) {
		Vehicle target = repository.findById(id).orElse(null);
		if(target == null) {
			return null;
		}
		
		String changeYtoN = "Y".equals(target.getVisibleYn()) ? "N" : "Y";
		
		Vehicle updated = Vehicle.builder()
			    .reservationNo(target.getReservationNo())
	            .facilityNo(target.getFacilityNo())
	            .employeeNo(target.getEmployeeNo())
	            .separatorCode(target.getSeparatorCode())
	            .reservationStart(target.getReservationStart())
	            .reservationEnd(target.getReservationEnd())
	            .visibleYn(changeYtoN) 
	            .reservationCreator(target.getReservationCreator())
	            .reservationEditor(target.getReservationEditor())
	            .reservationRegDate(target.getReservationRegDate())
	            .reservationModDate(target.getReservationModDate())
	            .reservationLocation(target.getReservationLocation())
	            .build();
		Vehicle saved = repository.save(updated);
		return new VehicleDto().toDto(saved);
	}
	
	//예약 시간 중복 alert
	public boolean isConflict(Long facilityNo, LocalDateTime start, LocalDateTime end) {
	    return repository.existsConflict(facilityNo, start, end);
	}
	
	//오늘 예약 현황
	public List<VehicleDto> getTodayReservations() {
	    List<Vehicle> entityList = repository.findTodayVehicleReservations();

	    List<VehicleDto> dtoList = new ArrayList<>();
	    for (Vehicle vehicle : entityList) {
	        VehicleDto dto = new VehicleDto().toDto(vehicle); // entity → dto 변환
	        dtoList.add(dto);
	    }

	    return dtoList;
	}
}
