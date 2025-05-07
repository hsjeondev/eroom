package com.eroom.notification.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.approval.dto.ApprovalAlarmDto;
import com.eroom.mail.dto.MailAlarmDto;
import com.eroom.notification.dto.AlarmDto;
import com.eroom.notification.dto.CalendarAlarmDto;
import com.eroom.notification.entity.Alarm;
import com.eroom.notification.repository.AlarmRepository;
import com.eroom.security.EmployeeDetails;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {
	
	private final AlarmRepository alarmRepository;

	 @Transactional
	 public List<AlarmDto> getAllAlarmsForUser() {
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
}
