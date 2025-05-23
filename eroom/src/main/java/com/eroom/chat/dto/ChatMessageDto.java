package com.eroom.chat.dto;

import java.time.LocalDateTime;

import com.eroom.chat.entity.ChatMessage;
import com.eroom.chat.entity.Chatroom;
import com.eroom.drive.entity.Drive;
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
    private String senderName; 		  // 보낸 사람 이름
    private Long receiverMember;      // 받은 사람 번호(Employee PK)
    private String chatMessageContent; // 메시지 내용
    private LocalDateTime messageRegDate; // 보낸 시간
    private String messageIsDeletedYn; // 삭제 여부 (Y/N);
    private Long driveAttachNo;     // 드라이브 번호
    private String drivePath; 
    private String driveOriName;
    private String senderProfileImageUrl;
    // DTO -> Entity
	public ChatMessage toEntity() {
		return ChatMessage.builder()
				.chatMessageNo(chatMessageNo)
				.chatroom(Chatroom.builder().chatroomNo(chatroomNo).build())
				.senderMember(Employee.builder().employeeNo(senderMember).build())
				.chatMessageContent(chatMessageContent)
				.messageRegDate(messageRegDate)
				.messageIsDeletedYn(messageIsDeletedYn)
				.drive(driveAttachNo != null ? Drive.builder().driveAttachNo(driveAttachNo).build() : null)
				.build();
			
	}
    // Entity -> DTO
	public static ChatMessageDto toDto(ChatMessage entity,String profileUrl) {
		return ChatMessageDto.builder().chatMessageNo(entity.getChatMessageNo())
				.chatroomNo(entity.getChatroom().getChatroomNo())
				.senderMember(entity.getSenderMember().getEmployeeNo())
				.senderName(entity.getSenderMember().getEmployeeName())
				.chatMessageContent(entity.getChatMessageContent())
				.messageRegDate(entity.getMessageRegDate())
				.messageIsDeletedYn(entity.getMessageIsDeletedYn())
				.driveAttachNo(entity.getDrive() != null ? entity.getDrive().getDriveAttachNo() : null)
		        .drivePath(entity.getDrive() != null ? entity.getDrive().getDrivePath() : null)
		        .driveOriName(entity.getDrive() != null ? entity.getDrive().getDriveOriName() : null)
		        .senderProfileImageUrl(profileUrl)
				.build();
	}
    
    
}