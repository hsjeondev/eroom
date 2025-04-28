package com.eroom.approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.approval.entity.ApprovalAlarm;

public interface ApprovalAlarmRepository extends JpaRepository<ApprovalAlarm, Long>  {

}
