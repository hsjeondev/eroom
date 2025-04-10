package com.eroom.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.mail.entity.Mail;

public interface MailRepository extends JpaRepository<Mail, Long>{

}
