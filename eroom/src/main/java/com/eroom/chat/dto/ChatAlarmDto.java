package com.eroom.chat.dto;

import java.time.LocalDateTime;

import com.eroom.chat.entity.ChatAlarm;
import com.eroom.chat.entity.ChatMessage;
import com.eroom.employee.entity.Employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChatAlarmDto {
    private Long chatAlarmNo; // 채팅 알림 번호
    private Long chatMessageNo; // 메시지 번호 (엔티티가 아닌 ID로만)
    private String chatAlarmReadYn; // 읽음 여부
    private LocalDateTime chatAlarmRegDate; // 알림 등록일
    private Long employeeNo; // 사원번호 (엔티티가 아닌 ID로만)
    private LocalDateTime chatAlarmReadDate; // 읽은 시간
    
    
    public ChatAlarm toEntity() {
        return ChatAlarm.builder()
                .chatAlarmNo(chatAlarmNo)
                .chatMessage(chatMessageNo != null ? ChatMessage.builder().chatMessageNo(chatMessageNo).build() : null)
                .chatAlarmReadYn(chatAlarmReadYn)
                .chatAlarmRegDate(chatAlarmRegDate)
                .chatAlarmReadDate(chatAlarmReadDate)
                .employee(employeeNo != null ? Employee.builder().employeeNo(employeeNo).build() : null)
                .build();
    }
    public ChatAlarmDto toDto(ChatAlarm entity) {
        return ChatAlarmDto.builder()
                .chatAlarmNo(entity.getChatAlarmNo())
                .chatMessageNo(entity.getChatMessage() != null ? entity.getChatMessage().getChatMessageNo() : null)
                .chatAlarmReadYn(entity.getChatAlarmReadYn())
                .chatAlarmRegDate(entity.getChatAlarmRegDate())
                .chatAlarmReadDate(entity.getChatAlarmReadDate())
                .employeeNo(entity.getEmployee() != null ? entity.getEmployee().getEmployeeNo() : null)
                .build();
    }

    
    
}
