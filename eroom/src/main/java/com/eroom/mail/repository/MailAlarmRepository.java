package com.eroom.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.mail.entity.MailAlarm;

public interface MailAlarmRepository extends JpaRepository<MailAlarm, Long>{

}
