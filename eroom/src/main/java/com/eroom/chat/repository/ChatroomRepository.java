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
	// 내가 생성한 채팅방 조회 (visibleYn = 'Y'만)
    @Query("SELECT c FROM Chatroom c WHERE c.creater = :creater AND c.visibleYn = 'Y'")
    List<Chatroom> findByCreater(@Param("creater") Employee creater);

    // 내가 참여한 채팅방 조회 (visibleYn = 'Y'만)
    @Query("SELECT ca.chatroomNo FROM ChatroomAttendee ca WHERE ca.attendee.employeeNo = :employeeNo AND ca.chatroomNo.visibleYn = 'Y'")
    List<Chatroom> findByParticipant(@Param("employeeNo") Long employeeNo);
    
}
