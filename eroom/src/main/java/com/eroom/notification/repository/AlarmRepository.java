package com.eroom.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.notification.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm,Long>, JpaSpecificationExecutor<Alarm> {

    @Query("""
            SELECT a FROM Alarm a
            LEFT JOIN FETCH a.calendarAlarm
            LEFT JOIN FETCH a.mailAlarm
            LEFT JOIN FETCH a.approvalAlarm
            LEFT JOIN FETCH a.chatAlarm
            WHERE a.employeeNo = :employeeNo
            ORDER BY a.alarmNo DESC
        """)
        List<Alarm> findAllWithDetailsByEmployeeNo(@Param("employeeNo") Long employeeNo);
}
