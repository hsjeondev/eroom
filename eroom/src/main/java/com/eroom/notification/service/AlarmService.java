package com.eroom.notification.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.approval.dto.ApprovalAlarmDto;
import com.eroom.calendar.dto.CalendarAlarmDto;
import com.eroom.chat.dto.ChatAlarmDto;
import com.eroom.mail.dto.MailAlarmDto;
import com.eroom.notification.dto.AlarmDto;
import com.eroom.notification.entity.Alarm;
import com.eroom.notification.repository.AlarmRepository;
import com.eroom.security.EmployeeDetails;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {
	
	private final AlarmRepository alarmRepository;
	
	 //알림 페이지 목록 조회
	 @Transactional
	 public List<AlarmDto> getMyAlarms() {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        EmployeeDetails user = (EmployeeDetails) auth.getPrincipal();
	        Long employeeNo = user.getEmployeeNo();

	        List<Alarm> alarms = alarmRepository.findAllWithDetailsByEmployeeNo(employeeNo);
	        List<AlarmDto> result = new ArrayList<>();

	        for (Alarm alarm : alarms) {
	            AlarmDto dto = new AlarmDto().toDto(alarm);

	            if ("R001".equals(alarm.getSeparatorCode()) && alarm.getCalendarAlarm() != null) {
	                dto.setCalendarAlarm(new CalendarAlarmDto().toDto(alarm.getCalendarAlarm()));
	            } else if ("R002".equals(alarm.getSeparatorCode()) && alarm.getChatAlarm() != null) {
	                dto.setChatAlarm(new ChatAlarmDto().toDto(alarm.getChatAlarm()));
	            } else if ("R003".equals(alarm.getSeparatorCode()) && alarm.getMailAlarm() != null) {
	                dto.setMailAlarm(new MailAlarmDto().toDto(alarm.getMailAlarm()));
	            } else if ("R004".equals(alarm.getSeparatorCode()) && alarm.getApprovalAlarm() != null) {
	                dto.setApprovalAlarm(new ApprovalAlarmDto().toDto(alarm.getApprovalAlarm()));
	            }

	            result.add(dto);
	        }

	        return result;
	    }
	 
	 //헤더 종 누르면 목록조회
	 public List<AlarmDto> getUnreadAlarms(Long loginEmployeeNo) {
		    List<Alarm> alarms = alarmRepository.findByEmployeeNoAndReadYnOrderByRegDateDesc(loginEmployeeNo, "N");

		    List<AlarmDto> result = new ArrayList<>();
		    for (Alarm alarm : alarms) {
		        AlarmDto dto = new AlarmDto().toDto(alarm);
		        result.add(dto);
		    }

		    return result;
		}
	 
	 //전체 읽음 처리 버튼
	 @Transactional
	 public void markAllAsRead(Long employeeNo) {
	     alarmRepository.updateAllToReadByEmployeeNo(employeeNo);
	 }
	 
	 //목록에서 일정 클릭하면 N을 Y로 변경
	 @Transactional
	 public void markAsRead(Long alarmId) {
	     Alarm target = alarmRepository.findById(alarmId).orElse(null);

	     if (target != null && "N".equals(target.getReadYn())) {
	         Alarm updated = Alarm.builder()
	             .alarmNo(target.getAlarmNo())
	             .param1(target.getParam1())
	             .separatorCode(target.getSeparatorCode())
	             .employeeNo(target.getEmployeeNo())
	             .readYn("Y") // 읽음 처리
	             .regDate(target.getRegDate())
	             .build();

	         alarmRepository.save(updated);
	     }
	 }
	 
	 @Transactional
	 public Map<String, List<AlarmDto>> getGroupedAlarms() {
	     //  로그인한 사용자 정보에서 employeeNo 추출
	     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	     EmployeeDetails user = (EmployeeDetails) auth.getPrincipal();
	     Long employeeNo = user.getEmployeeNo();

	     //  해당 직원의 모든 알림 가져오기
	     List<Alarm> alarms = alarmRepository.findAllWithDetailsByEmployeeNo(employeeNo);

	     // 날짜별 그룹핑 맵
	     Map<String, List<AlarmDto>> groupedMap = new LinkedHashMap<>();

	     for (Alarm alarm : alarms) {
	         AlarmDto dto = new AlarmDto().toDto(alarm);

	         // 상세 알림 설정 
	         if ("R001".equals(alarm.getSeparatorCode()) && alarm.getCalendarAlarm() != null) {
	             dto.setCalendarAlarm(new CalendarAlarmDto().toDto(alarm.getCalendarAlarm()));
	         } else if ("R002".equals(alarm.getSeparatorCode()) && alarm.getChatAlarm() != null) {
	             dto.setChatAlarm(new ChatAlarmDto().toDto(alarm.getChatAlarm()));
	         } else if ("R003".equals(alarm.getSeparatorCode()) && alarm.getMailAlarm() != null) {
	             dto.setMailAlarm(new MailAlarmDto().toDto(alarm.getMailAlarm()));
	         } else if ("R004".equals(alarm.getSeparatorCode()) && alarm.getApprovalAlarm() != null) {
	             dto.setApprovalAlarm(new ApprovalAlarmDto().toDto(alarm.getApprovalAlarm()));
	         }

	         //날짜 키 (yyyy-MM-dd)
	         String dateKey = dto.getReg_date().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

	         // 그룹에 추가
	         groupedMap.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(dto);
	     }

	     return groupedMap;
	 }
	 
}
