package com.eroom.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.eroom.mail.entity.MailStatus;

public interface MailStatusRepository extends JpaRepository<MailStatus, Long> {

}