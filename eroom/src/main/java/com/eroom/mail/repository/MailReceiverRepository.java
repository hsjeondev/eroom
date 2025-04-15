package com.eroom.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.mail.entity.MailReceiver;

public interface MailReceiverRepository extends JpaRepository<MailReceiver, Long>{

}
