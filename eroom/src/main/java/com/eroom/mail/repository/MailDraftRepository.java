package com.eroom.mail.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.mail.entity.MailDraft;

public interface MailDraftRepository extends JpaRepository<MailDraft, Long> {

	
}
