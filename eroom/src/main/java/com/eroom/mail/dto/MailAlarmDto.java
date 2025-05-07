package com.eroom.mail.dto;

import java.time.LocalDateTime;

import com.eroom.mail.entity.MailAlarm;
import com.eroom.mail.entity.MailReceiver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailAlarmDto {

    private Long mail_alarm_no;         // 알림 번호
    private Long mail_receiver_no;      // 수신자 번호 (MailReceiver에서 가져온 FK)
    @Builder.Default
    private String mail_alarm_read_yn="Y";   // 읽음 여부
    private LocalDateTime mail_alarm_reg_date;  // 생성일자
    
    public MailAlarm toEntity() {
        return MailAlarm.builder()
            .mailAlarmNo(mail_alarm_no)
            .mailReceiver(
                MailReceiver.builder()
                    .mailReceiverNo(mail_receiver_no)
                    .build()
            )
            .mailAlarmReadYn(mail_alarm_read_yn)
            .mailAlarmRegDate(mail_alarm_reg_date)
            .build();
    }
    public  MailAlarmDto toDto(MailAlarm mailAlarm) {
        return MailAlarmDto.builder()
            .mail_alarm_no(mailAlarm.getMailAlarmNo())
            .mail_receiver_no(
                mailAlarm.getMailReceiver().getMailReceiverNo()
            )
            .mail_alarm_read_yn(mailAlarm.getMailAlarmReadYn())
            .mail_alarm_reg_date(mailAlarm.getMailAlarmRegDate())
            .build();
    }
    
}