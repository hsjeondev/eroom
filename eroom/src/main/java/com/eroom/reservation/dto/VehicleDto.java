package com.eroom.reservation.dto;

import java.time.LocalDateTime;

import com.eroom.reservation.entity.Vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class VehicleDto {
	private Long reservation_no;
	private Long facility_no;
	private Long employee_no;
	private LocalDateTime reservation_start;
	private LocalDateTime reservation_end;
	private String visible_yn="Y";
	private String reservation_creator;
	private String reservation_editor;
	private LocalDateTime reservation_reg_date;
	private LocalDateTime reservation_mod_date;
	private String reservation_location;
	
	public Vehicle toEntity() {
		return Vehicle.builder()
				.reservationNo(reservation_no)
				.facilityNo(facility_no)
				.employeeNo(employee_no)
				.reservationStart(reservation_start)
				.reservationEnd(reservation_end)
				.visibleYn(visible_yn)
				.reservationCreator(reservation_creator)
				.reservationEditor(reservation_editor)
				.reservationRegDate(reservation_reg_date)
				.reservationModDate(reservation_mod_date)
				.reservationLocation(reservation_location)
				.build();
	}
	
	public VehicleDto toDto(Vehicle vehicle) {
		return VehicleDto.builder()
				.reservation_no(vehicle.getReservationNo())
				.facility_no(vehicle.getFacilityNo())
				.employee_no(vehicle.getEmployeeNo())
				.reservation_start(vehicle.getReservationStart())
				.reservation_end(vehicle.getReservationEnd())
				.visible_yn(vehicle.getVisibleYn())
				.reservation_creator(vehicle.getReservationCreator())
				.reservation_editor(vehicle.getReservationEditor())
				.reservation_reg_date(vehicle.getReservationRegDate())
				.reservation_mod_date(vehicle.getReservationModDate())
				.reservation_location(vehicle.getReservationLocation())
				.build();
	}
	
	
}
