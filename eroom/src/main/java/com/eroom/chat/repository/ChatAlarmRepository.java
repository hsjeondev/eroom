package com.eroom.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.chat.entity.ChatAlarm;

public interface ChatAlarmRepository extends JpaRepository<ChatAlarm, Long>{
	// 채팅 알림을 읽지 않은 알림 리스트 조회
	@Query("SELECT a FROM ChatAlarm a WHERE a.employee.employeeNo = :employeeNo AND a.chatMessage.chatroom.chatroomNo = :chatroomNo AND a.chatAlarmReadYn = 'N'")
	List<ChatAlarm> findUnreadAlarms(@Param("employeeNo") Long employeeNo, @Param("chatroomNo") Long chatroomNo);
	// 채팅 알림을 읽은 알림 리스트 조회
	@Query("SELECT COUNT(a) FROM ChatAlarm a WHERE a.employee.employeeNo = :employeeNo AND a.chatMessage.chatroom.chatroomNo = :chatroomNo AND a.chatAlarmReadYn = 'N'")
	int countUnreadAlarms(@Param("employeeNo") Long employeeNo, @Param("chatroomNo") Long chatroomNo);


}