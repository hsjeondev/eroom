package com.eroom.reservation.dto;

import com.eroom.reservation.entity.MeetingRoom;
import com.eroom.employee.entity.Employee;
import com.eroom.reservation.entity.ReservationEmployeeMapping;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEmployeeMappingDto {
    private Long mappingNo;
    private Long reservationNo;
    private Long employeeNo;

    public ReservationEmployeeMapping toEntity(MeetingRoom reservation, Employee employee) {
        return ReservationEmployeeMapping.builder()
                .mappingNo(this.mappingNo)
                .reservation(reservation)
                .employee(employee)
                .build();
    }
    
    public ReservationEmployeeMappingDto toDto(ReservationEmployeeMapping rem) {
        return ReservationEmployeeMappingDto.builder()
                .mappingNo(rem.getMappingNo())
                .reservationNo(rem.getReservation().getReservationNo()) 
                .employeeNo(rem.getEmployee().getEmployeeNo())         
                .build();
    }
}
