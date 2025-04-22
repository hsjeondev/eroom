package com.eroom.mail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.mail.entity.MailReceiver;

public interface MailReceiverRepository extends JpaRepository<MailReceiver, Long>{

	@Query("SELECT mr FROM MailReceiver mr JOIN FETCH mr.mail m WHERE mr.receiver.employeeNo = :empNo")
	List<MailReceiver> findByEmployeeNo(@Param("empNo") Long empNo);
	
	// 오래된 순
	@Query("SELECT mr FROM MailReceiver mr JOIN FETCH mr.mail m WHERE mr.receiver.employeeNo = :empNo ORDER BY m.mailSentTime ASC")
	List<MailReceiver> findByEmployeeNoOrderByOldest(@Param("empNo") Long empNo);
	
	// 최신순
	@Query("SELECT mr FROM MailReceiver mr JOIN FETCH mr.mail m WHERE mr.receiver.employeeNo = :empNo ORDER BY m.mailSentTime DESC")
	List<MailReceiver> findByEmployeeNoOrderByLatest(@Param("empNo") Long empNo);
	
	


	@Transactional
	@Modifying
	@Query("UPDATE MailReceiver mr SET mr.mailReceiverReadYn = 'Y' WHERE mr.mail.mailNo = :mailNo AND mr.receiver.employeeNo = :employeeNo")
	void updateReadYn(@Param("employeeNo") Long employeeNo,@Param("mailNo") Long mailNo);
}
