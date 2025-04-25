package com.eroom.reservation.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
	private String separator_code;
	private LocalDateTime reservation_start;
	private LocalDateTime reservation_end;
	private String visible_yn="Y";
	private String vehicleName;
	private String reserverName;
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
				.separatorCode(separator_code)
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
				.separator_code(vehicle.getSeparatorCode())
				.reservation_start(vehicle.getReservationStart())
				.reservation_end(vehicle.getReservationEnd())
				.visible_yn(vehicle.getVisibleYn())
				.reservation_creator(vehicle.getReservationCreator())
				.reservation_editor(vehicle.getReservationEditor())
				.reservation_reg_date(vehicle.getReservationRegDate())
				.reservation_mod_date(vehicle.getReservationModDate())
				.reservation_location(vehicle.getReservationLocation())
				.vehicleName(vehicle.getFacility() != null ? vehicle.getFacility().getFacilityName() : "")
		        .reserverName(vehicle.getEmployee() != null ? vehicle.getEmployee().getEmployeeName() : "")
				.build();
	}
	
	public Map<String, Object> toFullCalendarEvent() {
	    Map<String, Object> event = new HashMap<>();
	    event.put("title", "[" + this.vehicleName + "] " + this.reserverName);
	    event.put("start", this.reservation_start != null ? this.reservation_start.toString() : "");
	    event.put("end", this.reservation_end != null ? this.reservation_end.toString() : "");


	    // FullCalendar에서 사용할 정보들 추가
	    event.put("reservation_no", this.reservation_no);  
	    event.put("vehicleName", this.vehicleName);        
	    event.put("reserverName", this.reserverName);    
	    event.put("reservationLocation", this.reservation_location); 

	    event.put("separator", this.separator_code != null ? this.separator_code : "");
	    event.put("reservationCreator", this.reservation_creator != null ? this.reservation_creator : "");
	    event.put("type", "vehicle");

	    return event;
	}

	
	
}
