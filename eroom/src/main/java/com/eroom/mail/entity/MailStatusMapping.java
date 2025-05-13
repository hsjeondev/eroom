/*package com.eroom.mail.entity;

import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailStatusMapping {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mail_status_mapping_no")
    private Long mailStatusMappingNo;

    @ManyToOne
    @JoinColumn(name="mail_no")
    private Mail mail; // 메일 정보

    @ManyToOne
    @JoinColumn(name="mail_status_no")
    private MailStatus mailStatus; // 메일 상태 (읽음, 삭제됨, 중요 등)
}*/
