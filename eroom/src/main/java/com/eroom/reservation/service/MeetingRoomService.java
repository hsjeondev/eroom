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
import com.eroom.reservation.dto.MeetingRoomDto;
import com.eroom.reservation.entity.MeetingRoom;
import com.eroom.reservation.entity.ReservationEmployeeMapping;
import com.eroom.reservation.entity.Vehicle;
import com.eroom.reservation.repository.MeetingRoomRepository;
import com.eroom.reservation.repository.ReservationEmployeeMappingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {
	
	private final MeetingRoomRepository repository;
	private final EmployeeRepository employeeRepository;
	private final ReservationEmployeeMappingRepository mappingRepository;	
	
	//예약실 등록
	public MeetingRoomDto meetingRoomServiceReservation(MeetingRoomDto dto) {
		MeetingRoom param = dto.toEntity();
		MeetingRoom result = repository.save(param);
		return new MeetingRoomDto().toDto(result);
	}
	
	//참가자 등록
	 public void saveParticipants(Long reservationNo, List<Long> participantIds) {
	        // 예약 번호로 MeetingRoom 예약 조회
	        MeetingRoom meetingRoom = repository.findById(reservationNo).orElse(null);
	        if (meetingRoom == null) return;

	        // 참여자 ID 리스트 반복
	        for (Long empNo : participantIds) {
	            Employee employee = employeeRepository.findById(empNo).orElse(null);
	            if (employee != null) {
	                ReservationEmployeeMapping mapping = ReservationEmployeeMapping.builder()
	                    .reservation(meetingRoom)
	                    .employee(employee)
	                    .build();
	                mappingRepository.save(mapping);
	            }
	        }
	    }

	

	public List<String>getMeetingRoomTimes(String date,String FacilityNoStr){
		
			LocalDate targetDate = LocalDate.parse(date);
			LocalDateTime startOfDay = targetDate.atStartOfDay();
			LocalDateTime endOfDay = targetDate.plusDays(1).atStartOfDay();

			Long facilityNo = Long.parseLong(FacilityNoStr); // 
			List<MeetingRoom> reservedList = repository.findByFacilityNoAndReservationDate(facilityNo, startOfDay, endOfDay);

			Set<String> bookedTimesSet = new HashSet<>();
			for (MeetingRoom v : reservedList) {
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
	}

	


