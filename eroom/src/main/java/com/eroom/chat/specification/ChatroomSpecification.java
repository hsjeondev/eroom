package com.eroom.chat.specification;

import org.springframework.data.jpa.domain.Specification;

import com.eroom.chat.entity.Chatroom;
import com.eroom.directory.entity.Employee;

public class ChatroomSpecification {

	// 프롬멤버
	public static Specification<Chatroom> fromUserEquals(Employee employee){
		return (root,query, CriteriaBuilder) ->
			CriteriaBuilder.equal(root.get("formMember"),employee);
	}
	public static Specification<Chatroom> toUserEquals(Employee employee){
		return (root,query, CriteriaBuilder) ->
			CriteriaBuilder.equal(root.get("creater"),employee);
	}
	
}
