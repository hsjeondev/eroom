package com.eroom.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.eroom.notification.entity.CalendarAlarm;

public interface CalendarAlarmRepository extends JpaRepository<CalendarAlarm,Long>,JpaSpecificationExecutor<CalendarAlarm> {
	List<CalendarAlarm> findByEmployeeNoOrderByAlarmIdDesc(Long employeeNo);
	
	 List<CalendarAlarm> findByEmployeeNoAndAlarmReadYnAndSeparatorStartingWith(Long employeeNo, String alarmReadYn, String separatorPrefix);

}
