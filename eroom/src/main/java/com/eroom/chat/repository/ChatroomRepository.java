package com.eroom.chat.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.chat.entity.Chatroom;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long>{

	List<Chatroom> findAll(Specification<Chatroom> spec);
		
}
