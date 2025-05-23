package com.eroom.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.facility.entity.Facility;
import com.eroom.facility.repository.FacilityRepository;
import com.eroom.reservation.dto.MeetingRoomDto;
import com.eroom.reservation.entity.MeetingRoom;
import com.eroom.reservation.entity.ReservationEmployeeMapping;
import com.eroom.reservation.repository.MeetingRoomRepository;
import com.eroom.reservation.repository.ReservationEmployeeMappingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {
	
	private final MeetingRoomRepository repository;
	private final EmployeeRepository employeeRepository;
	private final ReservationEmployeeMappingRepository mappingRepository;	
	private final FacilityRepository facilityRepository;
	
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

	
	//예약 시간 막기
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
				for (int hour = start.getHour(); hour <= end.getHour(); hour++) {
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
	

	//회의실 이름, 참여자 이름 , 예약자 이름 가져오기
	public List<MeetingRoomDto> getMeetingRoomList(String separator){
	    List<MeetingRoom> list = repository.findBySeparatorCodeAndVisibleYn(separator,"Y");
	    List<MeetingRoomDto> dtoList = new ArrayList<>();

	    for (MeetingRoom meeting : list) {
	        MeetingRoomDto dto = new MeetingRoomDto().toDto(meeting);

	        Facility facility = facilityRepository.findByFacilityNo(meeting.getFacilityNo());
	        if (facility != null) {
	            dto.setMeetingRoomName(facility.getFacilityName()); //  회의실 이름 설정
	        }

	        Employee employee = employeeRepository.findByEmployeeNo(meeting.getEmployeeNo());
	        if (employee != null) {
	            dto.setReserverName(employee.getEmployeeName()); // 예약자 이름
	        }
	        
	        // 참여자 이름 리스트
	        List<ReservationEmployeeMapping> mappings = mappingRepository.findByReservation(meeting);
	        List<String> participantNames = new ArrayList<>();
	        for (ReservationEmployeeMapping mapping : mappings) {
	            Employee emp = mapping.getEmployee();
	            if (emp != null) {
	                participantNames.add(emp.getEmployeeName());
	            }
	        }
	        
	        dto.setParticipantNames(participantNames);  // 

	        dtoList.add(dto);
	    }

	    return dtoList;
	}
	
	public MeetingRoomDto deleteMeetingRoom(Long id) {
		MeetingRoom target = repository.findById(id).orElse(null);
		if(target == null) {
			return null;
		}
		
		String changeYtoN = "Y".equals(target.getVisibleYn()) ? "N" : "Y";
		
		MeetingRoom updated = MeetingRoom.builder()
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
		MeetingRoom saved = repository.save(updated);
		return new MeetingRoomDto().toDto(saved);
				
	}
	
    public boolean isConflict(Long facilityNo, LocalDateTime start, LocalDateTime end) {
        return repository.existsConflict(facilityNo, start, end);
    }

    public List<MeetingRoomDto> getTodayReservations() {
        List<MeetingRoom> entityList = repository.findTodayMeetingRoomReservations();

        List<MeetingRoomDto> dtoList = new ArrayList<>();
        for (MeetingRoom meetingRoom : entityList) {
            MeetingRoomDto dto = new MeetingRoomDto().toDto(meetingRoom); // entity → dto 변환
            dtoList.add(dto);
        }

        return dtoList;
    }
    
    // 회의실 전체 예약 현황
    public List<MeetingRoomDto> getAllReservations(){
    	List<MeetingRoom> entityList = repository.findByVisibleYnAndSeparatorCodeOrderByReservationStartDesc("Y", "F001");
    	
    	List<MeetingRoomDto> dtoList = new ArrayList<>();
    	for(MeetingRoom meetingRoom : entityList) {
    		MeetingRoomDto dto = new MeetingRoomDto().toDto(meetingRoom);
    		// 회의실 이름
    		Facility facility = facilityRepository.findByFacilityNo(meetingRoom.getFacilityNo());
    		if(facility != null) {
    			dto.setMeetingRoomName(facility.getFacilityName());
    		}
    		// 예약자 이름
    		Employee employee = employeeRepository.findByEmployeeNo(meetingRoom.getEmployeeNo());
    		if(employee != null) {
    			dto.setReserverName(employee.getEmployeeName());
    		}
    		
    		// 참여자
    		List<ReservationEmployeeMapping> mappings = mappingRepository.findByReservation(meetingRoom);
    		List<String> participantNames = new ArrayList<>();
    		for(ReservationEmployeeMapping mapping : mappings) {
    			if(mapping.getEmployee() != null) {
    				participantNames.add(mapping.getEmployee().getEmployeeName());
    			}
    		}
    		dto.setParticipantNames(participantNames);
    		
    		dtoList.add(dto);
    	}
    	return dtoList;
    }
	
		
}

