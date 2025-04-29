package com.eroom.mail.dto;

import java.time.LocalDateTime;

import com.eroom.employee.entity.Employee;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.entity.MailDraft;

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
@Builder
@ToString
public class MailDraftDto {

	private Long mail_draft_no;      // 메일 임시저장 번호 (PK)
    private Long mail_no;            // 메일 번호 (FK)
    private Long employee_no;        // 수신자 사원 번호 (FK)
    private LocalDateTime mail_draft_time; // 임시 저장한 시간

    private Mail mail;
	private Employee receiver;
    // Dto → Entity 변환
    public MailDraft toEntity() {
        return MailDraft.builder()
                .mailDraftNo(mail_draft_no)
                .mail(Mail.builder().mailNo(mail_no).build())
                .employee(Employee.builder().employeeNo(employee_no).build())
                .mailDraftTime(mail_draft_time)
                .build();
    }
	
}
