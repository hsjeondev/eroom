package com.eroom.mail.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.mail.entity.Mail;

public interface MailRepository extends JpaRepository<Mail, Long>{

//	List<Mail> findBySenderEmployeeNo(Long employeeNo);
	// 최신순
	List<Mail> findBySenderEmployeeNoOrderByMailSentTimeDesc(Long employeeNo);
	// 오래된 순
	List<Mail> findBySenderEmployeeNoOrderByMailSentTimeAsc(Long employeeNo);
	
	 Optional<Mail> findById(Long mailNo);
}
