/*package com.eroom.mail.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.mail.entity.MailStatusMapping;

public interface MailStatusMappingRepository extends JpaRepository<MailStatusMapping, Long> {
	
	Optional<MailStatusMapping> findByMail_mailNoAndMailStatus_mailStatusNo(Long mailNo, Long mailStatusNo);

    // 여러 메일에 대한 상태 조회
    List<MailStatusMapping> findByMail_mailNo(Long mailNo);
}*/
