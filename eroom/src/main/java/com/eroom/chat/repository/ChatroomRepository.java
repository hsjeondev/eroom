package com.eroom.chat.repository;

import java.util.List;
import java.util.Optional;

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
 
    @Query("SELECT c FROM Chatroom c JOIN FETCH c.chatroomMapping ca WHERE c.chatroomNo = :chatroomNo")
    Chatroom findByIdWithAttendees(@Param("chatroomNo") Long chatroomNo);
    
    @Query("SELECT c FROM Chatroom c " +
    	       "JOIN c.chatroomMapping m1 " +
    	       "JOIN c.chatroomMapping m2 " +
    	       "WHERE c.chatIsGroupYn = 'N' " +
    	       "AND m1.attendee.employeeNo = :emp1 " +
    	       "AND m2.attendee.employeeNo = :emp2 " +
    	       "AND SIZE(c.chatroomMapping) = 2")
    	Optional<Chatroom> findOneToOneRoom(@Param("emp1") Long emp1, @Param("emp2") Long emp2);

}