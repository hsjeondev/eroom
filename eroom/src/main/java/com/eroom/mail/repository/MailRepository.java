package com.eroom.mail.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.mail.entity.Mail;

public interface MailRepository extends JpaRepository<Mail, Long>{

//	List<Mail> findBySenderEmployeeNo(Long employeeNo);
	// 최신순
	//List<Mail> findBySenderEmployeeNoOrderByMailSentTimeDesc(Long employeeNo);
	/*
	@Query("SELECT m FROM Mail m " +
		       "WHERE m.sender.employeeNo = :employeeNo " +
		       "AND m.mailNo NOT IN (SELECT d.mail.mailNo FROM MailDraft d) " +
		       "ORDER BY m.mailSentTime DESC")
	List<Mail> findSentMailsLatest(@Param("employeeNo") Long employeeNo);
	// 오래된 순
	//List<Mail> findBySenderEmployeeNoOrderByMailSentTimeAsc(Long employeeNo);
	@Query("SELECT m FROM Mail m " +
		       "WHERE m.sender.employeeNo = :employeeNo " +
		       "AND m.mailNo NOT IN (SELECT d.mail.mailNo FROM MailDraft d) " +
		       "ORDER BY m.mailSentTime ASC")
		List<Mail> findSentMailsOldest(@Param("employeeNo") Long employeeNo);
	*/
	@Query("SELECT m FROM Mail m " +
		       "WHERE m.sender.employeeNo = :employeeNo " +
		       "AND m.mailVisibleYn = 'Y' " +
		       "AND m.mailNo NOT IN (SELECT d.mail.mailNo FROM MailDraft d) " +
		       "ORDER BY m.mailSentTime DESC")
		List<Mail> findSentMailsLatest(@Param("employeeNo") Long employeeNo);

		// 오래된 순
		@Query("SELECT m FROM Mail m " +
		       "WHERE m.sender.employeeNo = :employeeNo " +
		       "AND m.mailVisibleYn = 'Y' " +
		       "AND m.mailNo NOT IN (SELECT d.mail.mailNo FROM MailDraft d) " +
		       "ORDER BY m.mailSentTime ASC")
		List<Mail> findSentMailsOldest(@Param("employeeNo") Long employeeNo);
	 Optional<Mail> findById(Long mailNo);
	
	// 임시 보관함
	 @Query("SELECT m FROM Mail m " +
		       "WHERE m.sender.employeeNo = :employeeNo " +  // 내가 보낸 메일
		       "AND m.mailNo IN (SELECT d.mail.mailNo FROM MailDraft d WHERE d.mail.mailNo = m.mailNo) " + // 해당 메일이 임시 저장된 메일인지 확인
		       "ORDER BY m.mailSentTime DESC")  // 최신순
		List<Mail> findDraftMailsLatest(@Param("employeeNo") Long employeeNo);
	 
	 @Query("SELECT m FROM Mail m " +
		       "WHERE m.sender.employeeNo = :employeeNo " +  // 내가 보낸 메일
		       "AND m.mailNo IN (SELECT d.mail.mailNo FROM MailDraft d WHERE d.mail.mailNo = m.mailNo) " + // 해당 메일이 임시 저장된 메일인지 확인
		       "ORDER BY m.mailSentTime ASC")  // 오래된 순
		List<Mail> findDraftMailsOldest(@Param("employeeNo") Long employeeNo);
	 
	// 답장 
	 @Query("SELECT m.sender.employeeNo FROM Mail m WHERE m.mailNo = :mailNo")
	 Long findSenderEmployeeNoByMailNo(@Param("mailNo") Long mailNo);
}
