package com.eroom.mail.dto;

import java.time.LocalDateTime;

import com.eroom.employee.entity.Employee;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.entity.MailStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailStatusDto {

	private Long mail_status_no;
	@Builder.Default
    private String mail_status_deleted_yn="N";
	@Builder.Default
	private String mail_status_important_yn="N";
    private LocalDateTime mail_status_deleted_time;
    @Builder.Default
    private String mail_status_visible_yn="Y";
    
    private Long employee_no;
    private Long mail_no;
    public MailStatus toEntity() {
    	MailStatus mailStatus = MailStatus.builder()
    							.mailStatusNo(mail_status_no)
    							.mail(Mail.builder().mailNo(mail_no).build())
    							.employee(Employee.builder().employeeNo(employee_no).build())
    							.mailStatusDeletedYn(mail_status_deleted_yn)
    							.mailStatusImportantYn(mail_status_important_yn)
    							.maillStatusVisibleYn(mail_status_visible_yn)
    							.build();
    	return mailStatus;
    }
    
}
