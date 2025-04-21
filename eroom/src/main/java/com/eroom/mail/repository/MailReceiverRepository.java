package com.eroom.mail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.mail.entity.MailReceiver;

public interface MailReceiverRepository extends JpaRepository<MailReceiver, Long>{

	@Query("SELECT mr FROM MailReceiver mr JOIN FETCH mr.mail m WHERE mr.receiver.employeeNo = :empNo")
	List<MailReceiver> findByEmployeeNo(@Param("empNo") Long empNo);
}
