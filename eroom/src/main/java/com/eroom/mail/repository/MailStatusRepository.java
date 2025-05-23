package com.eroom.mail.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.mail.entity.Mail;
import com.eroom.mail.entity.MailStatus;

import jakarta.transaction.Transactional;

public interface MailStatusRepository extends JpaRepository<MailStatus, Long> {

	Optional<MailStatus> findByMail_mailNoAndEmployee_employeeNo(Long mailNo, Long employeeNo);

	List<MailStatus> findByMailMailNoIn(List<Long> mailNos);
	
	// 휴지통 조회
	/*
	@Query("SELECT s.mail FROM MailStatus s " +
		       "WHERE s.employee.employeeNo = :empNo " +
		       "AND s.mailStatusDeletedYn = 'Y' " +
		       "AND s.mail.mailVisibleYn = 'Y' " +
		       "ORDER BY s.mail.mailSentTime DESC")
		List<Mail> findAllDeletedMailsByEmployeeLatest(@Param("empNo") Long empNo);

		// 오래된순
		@Query("SELECT s.mail FROM MailStatus s " +
		       "WHERE s.employee.employeeNo = :empNo " +
		       "AND s.mailStatusDeletedYn = 'Y' " +
		       "AND s.mail.mailVisibleYn = 'Y' " +
		       "ORDER BY s.mail.mailSentTime ASC")
		List<Mail> findAllDeletedMailsByEmployeeOldest(@Param("empNo") Long empNo);
	*/
	// 지금 상황에서 FK값
	@Query("SELECT s.mail FROM MailStatus s " +
		       "WHERE s.employee.employeeNo = :empNo " +
		       "AND s.mailStatusDeletedYn = 'Y' " +
		       "AND s.mailStatusVisibleYn = 'Y' " +
		       "AND s.mail.mailVisibleYn = 'Y' " +
		       "ORDER BY s.mailStatusNo DESC")
		List<Mail> findAllDeletedMailsByEmployeeLatest(@Param("empNo") Long empNo);

		// 오래된순
		@Query("SELECT s.mail FROM MailStatus s " +
		       "WHERE s.employee.employeeNo = :empNo " +
		       "AND s.mailStatusDeletedYn = 'Y' " +
		       "AND s.mailStatusVisibleYn = 'Y' " +
		       "AND s.mail.mailVisibleYn = 'Y' " +
		       "ORDER BY s.mailStatusNo ASC")
		List<Mail> findAllDeletedMailsByEmployeeOldest(@Param("empNo") Long empNo);
	
	// 중요 메일함
		// 상태 메일함 시간 그냥 다르게 측정할까
	@Query("SELECT s.mail FROM MailStatus s " +
		       "WHERE s.employee.employeeNo = :empNo " +
		       "AND s.mailStatusImportantYn = 'Y' " +
		       "AND s.mailStatusVisibleYn = 'Y' " +
		       "AND s.mail.mailVisibleYn = 'Y' " +
		       "ORDER BY s.mail.mailSentTime DESC")
	List<Mail> findAllImportantMailsByEmployeeLatest(@Param("empNo") Long empNo);

	// 오래된순
	@Query("SELECT s.mail FROM MailStatus s " +
	       "WHERE s.employee.employeeNo = :empNo " +
	       "AND s.mailStatusImportantYn = 'Y' " +
	       "AND s.mailStatusVisibleYn = 'Y' " +
	       "AND s.mail.mailVisibleYn = 'Y' " +
	       "ORDER BY s.mail.mailSentTime ASC")
	List<Mail> findAllImportantMailsByEmployeeOldest(@Param("empNo") Long empNo);

	@Transactional
	@Modifying
	@Query("UPDATE MailStatus ms " +
	       "SET ms.mailStatusVisibleYn = 'N' " +
	       "WHERE ms.mailStatusDeletedYn = 'Y' " +
	       "AND ms.mailStatusDeletedTime <= :limitDate")
	void updateVisibilityOfOldTrash(@Param("limitDate") LocalDateTime limitDate);	
	
	// 휴지통에서 삭제
	@Modifying
	@Query("UPDATE MailStatus ms SET ms.mailStatusVisibleYn = :visibleYn WHERE ms.mail.mailNo = :mailNo AND ms.employee.employeeNo = :employeeNo")
	void updateVisibleYn(@Param("employeeNo") Long employeeNo, 
	                     @Param("mailNo") Long mailNo, 
	                     @Param("visibleYn") String visibleYn);
	
	
}