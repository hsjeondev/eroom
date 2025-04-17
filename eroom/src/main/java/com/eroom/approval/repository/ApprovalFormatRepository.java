package com.eroom.approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.approval.entity.ApprovalFormat;

public interface ApprovalFormatRepository extends JpaRepository<ApprovalFormat, Long> {

}
