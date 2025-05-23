package com.eroom.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.chat.entity.ChatroomAttendee;

public interface ChatroomAttendeeRepository extends JpaRepository<ChatroomAttendee, Long> {
    
    // 1대1 채팅방 조회
    @Query("SELECT c.chatroomNo FROM Chatroom c " +
           "JOIN ChatroomAttendee ca ON c.chatroomNo = ca.chatroomNo.chatroomNo " +
           "WHERE c.chatIsGroupYn = 'N' " +
           "AND c.creater.employeeNo = :myId " +
           "AND ca.attendee.employeeNo = :otherId")
    List<Long> findOneToOneChatroomNos(@Param("myId") Long myId, @Param("otherId") Long otherId);

    // 특정 채팅방에 특정 참여자가 있는지 확인
    @Query("SELECT ca FROM ChatroomAttendee ca WHERE ca.chatroomNo.chatroomNo = :chatroomNo AND ca.attendee.employeeNo = :employeeNo")
    ChatroomAttendee findByChatroomNoAndAttendeeEmployeeNo(@Param("chatroomNo") Long chatroomNo, @Param("employeeNo") Long employeeNo);

    // 특정 채팅방에 남아있는 참여자 수
    @Query("SELECT COUNT(ca) FROM ChatroomAttendee ca WHERE ca.chatroomNo.chatroomNo = :chatroomNo")
    long countByChatroomNo(@Param("chatroomNo") Long chatroomNo);

}
