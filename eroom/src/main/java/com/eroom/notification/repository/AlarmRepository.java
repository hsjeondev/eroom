package com.eroom.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.chat.entity.ChatAlarm;
import com.eroom.notification.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm,Long>, JpaSpecificationExecutor<Alarm> {
	
	//알림 페이지에 R001~R004 모든 알림 나오게
    @Query("""
            SELECT a FROM Alarm a
            LEFT JOIN FETCH a.calendarAlarm
            LEFT JOIN FETCH a.mailAlarm
            LEFT JOIN FETCH a.approvalAlarm
            LEFT JOIN FETCH a.chatAlarm
            WHERE a.employeeNo = :employeeNo
            ORDER BY a.regDate DESC
        """)
        List<Alarm> findAllWithDetailsByEmployeeNo(@Param("employeeNo") Long employeeNo);
    
    //종 누르면 목록에 알림 표현 N 인 것들만 조회
    List<Alarm> findByEmployeeNoAndReadYnOrderByRegDateDesc(Long employeeNo, String alarmReadYn);
 
    
    //전체 읽음 처리
    @Modifying
    @Query("UPDATE Alarm a SET a.readYn = 'Y' WHERE a.employeeNo = :employeeNo AND a.readYn = 'N'")
    int updateAllToReadByEmployeeNo(@Param("employeeNo") Long employeeNo);
    
}