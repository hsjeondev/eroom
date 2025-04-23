package com.eroom.reservation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	        // ✅ 먼저 toDto() 호출해서 기본값 세팅
	        VehicleDto dto = new VehicleDto().toDto(vehicle);

	        // 🚗 facility_name 가져오기
	        Facility facility = facilityRepositroy.findByFacilityNo(vehicle.getFacilityNo());
	        if (facility != null) {
	            dto.setVehicleName(facility.getFacilityName()); // 차량 이름
	        }

	        // 👤 employee_name 가져오기
	        Employee employee = employeeRepository.findByEmployeeNo(vehicle.getEmployeeNo());
	        if (employee != null) {
	            dto.setReserverName(employee.getEmployeeName()); // 예약자 이름
	        }

	        dtoList.add(dto); //
	    }

	    return dtoList;
	}
	
	public List<String> getBookedTimes(String date, String facilityNo) {
	    LocalDate targetDate = LocalDate.parse(date);
	    List<Vehicle> reservedList = repository.findByFacilityNoAndReservationDate(facilityNo, targetDate);

	    return reservedList.stream()
	            .map(v -> v.getReservationStart().toLocalTime().toString()) // 예: "09:00"
	            .distinct()
	            .sorted()
	            .collect(Collectors.toList());
	}
}
