package com.eroom.mail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.mail.entity.Mail;

public interface MailRepository extends JpaRepository<Mail, Long>{

//	List<Mail> findBySenderEmployeeNo(Long employeeNo);
	// 최신순
	List<Mail> findBySenderEmployeeNoOrderByMailSentTimeDesc(Long employeeNo);
	// 오래된 순
	List<Mail> findBySenderEmployeeNoOrderByMailSentTimeAsc(Long employeeNo);
	

}
