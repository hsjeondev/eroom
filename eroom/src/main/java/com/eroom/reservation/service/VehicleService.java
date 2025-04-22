package com.eroom.reservation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.calendar.dto.EmployeeCalendarDto;
import com.eroom.calendar.entity.EmployeeCalendar;
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
	
	public List<VehicleDto> getVehicleList(String separator){
		 List<Vehicle> list = repository.findBySeparatorCodeAndVisibleYn(separator,"Y");
		    List<VehicleDto> dtoList = new ArrayList<>();

		    for (Vehicle vehicle : list) {
		        VehicleDto dto = new VehicleDto();
		        dtoList.add(dto.toDto(vehicle));
		    }

		    return dtoList;
	}
}
