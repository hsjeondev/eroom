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
	
	

	// 읽음 처리
	@Transactional
	@Modifying
	@Query("UPDATE MailReceiver mr SET mr.mailReceiverReadYn = 'Y' WHERE mr.mail.mailNo = :mailNo AND mr.receiver.employeeNo = :employeeNo")
	void updateReadYn(@Param("employeeNo") Long employeeNo,@Param("mailNo") Long mailNo);
	
	// 휴지통 처리
	/*
	 * @Transactional
	 * 
	 * @Modifying
	 * 
	 * @Query("UPDATE MailReceiver mr SET mr.mailReceiverDeletedYn = 'Y' WHERE mr.receiver.employeeNo = :employeeNo AND mr.mail.mailNo = :mailNo"
	 * ) void updateDeletedYn(@Param("employeeNo") Long employeeNo, @Param("mailNo")
	 * Long mailNo);
	 */
	@Transactional
	@Modifying
	@Query("UPDATE MailReceiver mr SET mr.mailReceiverDeletedYn = 'Y', mr.mailReceiverDeletedTime = CURRENT_TIMESTAMP " +
	       "WHERE mr.receiver.employeeNo = :employeeNo AND mr.mail.mailNo = :mailNo")
	void updateDeletedYnAndTime(@Param("employeeNo") Long employeeNo, @Param("mailNo") Long mailNo);
	
	// delete문 안쓰기로 했지만 mail 완전히 삭제 로직
	@Modifying
    @Transactional
    @Query("DELETE FROM MailReceiver mr WHERE mr.mailReceiverNo = :mailReceiverNo AND mr.receiver.employeeNo = :employeeNo")
    void deleteByIdAndEmployeeNo(@Param("mailReceiverNo") Long mailReceiverNo, @Param("employeeNo") Long employeeNo);
}
