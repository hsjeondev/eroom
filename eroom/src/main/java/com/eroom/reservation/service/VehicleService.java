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
	public List<String> getBookedTimes(String date, String facilityNoStr) {
		LocalDate targetDate = LocalDate.parse(date);
		LocalDateTime startOfDay = targetDate.atStartOfDay();
		LocalDateTime endOfDay = targetDate.plusDays(1).atStartOfDay();

		Long facilityNo = Long.parseLong(facilityNoStr); // ← Long 변환 잊지 마!
		List<Vehicle> reservedList = repository.findByFacilityNoAndReservationDate(facilityNo, startOfDay, endOfDay);

		Set<String> bookedTimesSet = new HashSet<>();
		for (Vehicle v : reservedList) {
			LocalTime start = v.getReservationStart().toLocalTime();
			LocalTime end = v.getReservationEnd().toLocalTime();
			for (int hour = start.getHour(); hour < end.getHour(); hour++) {
				bookedTimesSet.add(String.format("%02d:00", hour));
			}
		}

		List<String> bookedTimes = new ArrayList<>();
		for (String time : bookedTimesSet) {
			bookedTimes.add(time);
		}
		Collections.sort(bookedTimes);
		return bookedTimes;
	}
	
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
}
