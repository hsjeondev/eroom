package com.eroom.mail.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.mail.dto.MailDto;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.repository.MailRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

	private final MailRepository repository;
	
	public List<Mail> selectMailAll(){
		List<Mail> list = repository.findAll();
		return list;
	}
	
	// 메일 생성
	public int createMail(MailDto mailDto) {
		int result = 0;
		try {
			Mail entity = mailDto.toEntity();
			Mail saver = repository.save(entity);
			
			result =1;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
