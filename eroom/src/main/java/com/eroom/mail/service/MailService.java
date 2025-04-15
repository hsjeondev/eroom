package com.eroom.mail.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.mail.dto.MailDto;
import com.eroom.mail.dto.MailReceiverDto;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.entity.MailReceiver;
import com.eroom.mail.repository.MailReceiverRepository;
import com.eroom.mail.repository.MailRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

	private final MailRepository repository;
	private final MailReceiverRepository mailRecevierRepository;
	

    
	public List<Mail> selectMailAll(){
		List<Mail> list = repository.findAll();
		return list;
	}
	
	// 메일 생성
	public int createMail(MailDto mailDto) {
		int result = 0;
		try {
			// 보낸 메일 저장
			Mail mailEntity = mailDto.toEntity();
			Mail mailSaver = repository.save(mailEntity);
			
			
			// 전송 메일 저장
			MailReceiver mailReceiverEntity = MailReceiver.builder()
//					.receiver(mailEntity.getReceivers())
					.mail(mailSaver)
					.build();
			mailRecevierRepository.save(mailReceiverEntity);
			
			//List<String> receiverList = Arrays.asList(mailDto.getReceiver().split(","));

			//for (String receiver : receiverList) {
//			    MailReceiver mailReceiver = MailReceiver.builder()
//			        .mail(saver)
//			        .receiver(mailDto.getEmployee_no()) // 혹은 receiverId 등
//			        .build();
//
//			    mailRecevierRepository.save(mailReceiver);
			//}
			
			
			result =1;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
