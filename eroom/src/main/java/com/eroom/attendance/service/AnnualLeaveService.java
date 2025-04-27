package com.eroom.attendance.service;

import org.springframework.stereotype.Service;

import com.eroom.attendance.repository.AnnualLeaveRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnualLeaveService {
	private final AnnualLeaveRepository annualLeaveRepository;

}
