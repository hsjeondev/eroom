package com.eroom.chat.specification;

import org.springframework.data.jpa.domain.Specification;

import com.eroom.chat.entity.Chatroom;
import com.eroom.employee.entity.Employee;

public class ChatroomSpecification {

	// 프롬멤버
	public static Specification<Chatroom> fromUserEquals(Employee employee){
		return (root,query, CriteriaBuilder) ->
			CriteriaBuilder.equal(root.get("creater"),employee);
	}
//	// 투멤버
	public static Specification<Chatroom> toUserEquals(Employee employee){
		return null;
//		return (root,query, CriteriaBuilder) ->
//			CriteriaBuilder.equal(root.get(""),employee);
	}
	
}
