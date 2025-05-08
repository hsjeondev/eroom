package com.eroom.calendar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.calendar.entity.CalendarAlarm;

public interface CalendarAlarmRepository extends JpaRepository<CalendarAlarm,Long>,JpaSpecificationExecutor<CalendarAlarm> {
	 List<CalendarAlarm> findByEmployeeNoOrderByAlarmIdDesc(Long employeeNo);
	
	 List<CalendarAlarm> findByEmployeeNoAndAlarmReadYnAndSeparatorStartingWith(Long employeeNo, String alarmReadYn, String separatorPrefix);
	 
	 List<CalendarAlarm> findByEmployeeNoOrderByAlarmRegDateDesc(Long employeeNo);
	 
	 List<CalendarAlarm> findByEmployeeNoAndAlarmReadYnOrderByAlarmRegDateDesc(Long employeeNo, String alarmReadYn);
	 
//	 @Modifying
//	 @Query("UPDATE CalendarAlarm a SET a.alarmReadYn = 'Y' WHERE a.employeeNo = :employeeNo AND a.alarmReadYn = 'N'")
//	 int updateAllToReadByEmployeeNo(@Param("employeeNo") Long employeeNo);
}
