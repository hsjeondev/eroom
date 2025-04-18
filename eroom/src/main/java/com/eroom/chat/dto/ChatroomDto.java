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
    private String chatroomReadYn = "N";
    
   
    // 참여자 ID 리스트
    private List<Long> participantIds;
    
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
        return ChatroomDto.builder()
                .chatroomNo(entity.getChatroomNo())
                .chatroomName(entity.getChatroomName())
                .chatIsGroupYn(entity.getChatIsGroupYn())
                .creater(entity.getCreater().getEmployeeNo())
                .chatroomRegDate(entity.getChatroomRegDate())
                .chatroomModDate(entity.getChatroomModDate())
                .chatLastMessage(entity.getChatLastMessage())
                .chatroomReadYn(entity.getChatroomReadYn())
                .build();
    }
}
