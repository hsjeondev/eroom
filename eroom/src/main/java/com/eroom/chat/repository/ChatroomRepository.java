package com.eroom.chat.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.chat.entity.Chatroom;
import com.eroom.employee.entity.Employee;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long>{
	// 채팅방 전체 조회
	List<Chatroom> findAll(Specification<Chatroom> spec);
	// 채팅방 생성자 조회
	List<Chatroom> findByCreater(Employee creater); 
	// 1대1 채팅방 조회
    @Query("SELECT ca.chatroomNo FROM ChatroomAttendee ca WHERE ca.attendee.employeeNo = :employeeNo")
    List<Chatroom> findByParticipant(@Param("employeeNo") Long employeeNo);
}
