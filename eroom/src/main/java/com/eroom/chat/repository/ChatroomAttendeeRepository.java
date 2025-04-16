package com.eroom.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.chat.entity.ChatroomAttendee;

public interface ChatroomAttendeeRepository extends JpaRepository<ChatroomAttendee, Long>{

}
