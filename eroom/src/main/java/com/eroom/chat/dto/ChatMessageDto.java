package com.eroom.chat.dto;

import java.time.LocalDateTime;

import com.eroom.chat.entity.ChatMessage;
import com.eroom.chat.entity.Chatroom;
import com.eroom.employee.entity.Employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ChatMessageDto {
	private Long chatMessageNo;      // 채팅 메시지 번호 
    private Long chatroomNo;          // 채팅방 번호
    private Long senderMember;        // 보낸 사람 번호(Employee PK)
    private Long receiverMember;      // 받은 사람 번호(Employee PK)
    private String chatMessageContent; // 메시지 내용
    private LocalDateTime messageRegDate; // 보낸 시간
    private String messageIsDeletedYn; // 삭제 여부 (Y/N);
    
    // DTO -> Entity
	public ChatMessage toEntity() {
		return ChatMessage.builder()
				.chatMessageNo(chatMessageNo)
				.chatroomNo(Chatroom.builder().chatroomNo(chatroomNo).build())
				.senderMember(Employee.builder().employeeNo(senderMember).build())
				.chatMessageContent(chatMessageContent)
				.messageRegDate(messageRegDate)
				.messageIsDeletedYn(messageIsDeletedYn).build();
	}
    // Entity -> DTO
	public static ChatMessageDto toDto(ChatMessage entity) {
		return ChatMessageDto.builder().chatMessageNo(entity.getChatMessageNo())
				.chatroomNo(entity.getChatroomNo().getChatroomNo())
				.senderMember(entity.getSenderMember().getEmployeeNo())
				.chatMessageContent(entity.getChatMessageContent())
				.messageRegDate(entity.getMessageRegDate())
				.messageIsDeletedYn(entity.getMessageIsDeletedYn()).build();
	}
    
    
}