package com.eroom.reservation.service;

import java.util.ArrayList;
import java.util.List;

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
		        VehicleDto dto = new VehicleDto();
		        
		        Facility facility = facilityRepositroy.findByFacilityNo(vehicle.getFacilityNo());
		        if (facility != null) {
		            dto.setVehicleName(facility.getFacilityName()); // 차량 이름
		        }

		        // employee_name 가져오기
		        Employee employee = employeeRepository.findByEmployeeNo(vehicle.getEmployeeNo());
		        if (employee != null) {
		            dto.setReserverName(employee.getEmployeeName()); // 예약자 이름
		        }

		        dtoList.add(dto.toDto(vehicle));
		        
		    }

		    return dtoList;
	}
}
