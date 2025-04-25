package com.eroom.mail.service;

import org.springframework.stereotype.Service;

import com.eroom.drive.repository.DriveRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailDriveService {
	
	private final DriveRepository driveRepository;
	
}
