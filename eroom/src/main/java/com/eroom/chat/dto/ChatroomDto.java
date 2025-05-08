package com.eroom.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

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
public class ChatroomDto {
	private Long chatroomNo;
	private Long creater; // 사번 Employeeno
    private String chatroomName;
    private String chatIsGroupYn;
    private LocalDateTime chatroomRegDate;
    private LocalDateTime chatroomModDate;
    private String chatLastMessage;
    private Integer unreadCount; // 읽지 않은 메시지 수
    private LocalDateTime lastMessageRegDate;
    private String profileImageUrl;
    private List<String> participantProfiles;
    // 참여자 ID 리스트
    private List<Long> participantIds;
    // 채팅 메시지 리스트
    private List<ChatMessageDto> messageList;
    
    public Chatroom toEntity() {
        return Chatroom.builder()
                .chatroomNo(chatroomNo)
                .chatroomName(chatroomName)
                .chatIsGroupYn(chatIsGroupYn)
                .creater(Employee.builder().employeeNo(creater).build())
                .chatroomRegDate(chatroomRegDate)
                .build();
    }
    public static ChatroomDto toDto(Chatroom entity) {
    	 List<Long> participants = new java.util.ArrayList<>();
    	    if (entity.getChatroomMapping() != null) {
    	        for (var attendee : entity.getChatroomMapping()) {
    	            participants.add(attendee.getAttendee().getEmployeeNo());
    	        }
    	    }
        return ChatroomDto.builder()
                .chatroomNo(entity.getChatroomNo())
                .chatroomName(entity.getChatroomName())
                .chatIsGroupYn(entity.getChatIsGroupYn())
                .creater(entity.getCreater().getEmployeeNo())
                .chatroomRegDate(entity.getChatroomRegDate())
                .chatroomModDate(entity.getChatroomModDate())
                .chatLastMessage(entity.getChatLastMessage())
                .lastMessageRegDate(entity.getLastMessageRegDate())
                .participantIds(participants)
                .build();
    }
    public static ChatroomDto toDto(Chatroom entity, List<ChatMessageDto> messages) {
        return ChatroomDto.builder()
                .chatroomNo(entity.getChatroomNo())
                .chatroomName(entity.getChatroomName())
                .chatIsGroupYn(entity.getChatIsGroupYn())
                .creater(entity.getCreater().getEmployeeNo())
                .chatroomRegDate(entity.getChatroomRegDate())
                .chatroomModDate(entity.getChatroomModDate())
                .chatLastMessage(entity.getChatLastMessage())
                .lastMessageRegDate(entity.getLastMessageRegDate())
                .messageList(messages)  // 메시지 리스트 추가
                .build();
    }
    

}