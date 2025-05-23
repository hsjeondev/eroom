package com.eroom.mail.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.mail.entity.MailDraft;

public interface MailDraftRepository extends JpaRepository<MailDraft, Long> {

	@Query("SELECT md FROM MailDraft md WHERE md.mail.mailNo = :mailNo")
	List<MailDraft> findByMailNoInMail(@Param("mailNo") Long mailNo);
	
	@Query("SELECT d FROM MailDraft d WHERE d.mail.mailNo = :mailNo")
	List<MailDraft> findByMailNo(@Param("mailNo") Long mailNo);
	
	Optional<MailDraft> findByMail_MailNo(Long mailNo);
}
