package com.eroom.mail.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.directory.entity.Directory;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.mail.dto.MailDto;
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
	private final EmployeeRepository employeeRepository;

    
	public List<Mail> selectMailAll(){
		List<Mail> list = repository.findAll();
		return list;
	}
	
	public List<Employee> selectEmployeeAll(){
		List<Employee> list = employeeRepository.findAll();
		return list;
	}
	
	// 메일 생성
	@Transactional
	public int createMail(MailDto mailDto) {
		int result = 0;
		try {
			// 보낸 메일 저장
			Mail mailEntity = mailDto.toEntity();
			Mail mailSaver = repository.save(mailEntity);
			
			
			 List<Long> receiverNos = mailDto.getReceiverNos();
		        for (Long receiverNo : receiverNos) {
		            // FK 관계 맞춰줌
		            Employee receiver = employeeRepository.findById(receiverNo).orElseThrow(() -> 
		                new IllegalArgumentException("존재하지 않는 사원 번호: " + receiverNo));
		            
		            Directory receiverDirectory = receiver.getDirectory();
		            
		            MailReceiver mailReceiver = MailReceiver.builder()
		                    .mail(mailSaver) // 발송된 메일
		                    .receiver(receiver) // 수신자 (Employee)
		                    .directory(receiverDirectory) // 수신자 Directory 정보
		                    .build();

		            mailRecevierRepository.save(mailReceiver);
		        }
			
			
			
			
			
			
			
			/*// 전송 메일 저장
			MailReceiverDto receiverDto=null;
			MailReceiver mailReceiverEntity = receiverDto.toEntity();
			MailReceiver mailReceiverSaver = MailReceiver.builder()
//					.receiver(mailDto.getReceiverNos())
					.mail(mailSaver)
					.build();
			mailRecevierRepository.save(mailReceiverEntity);*/
			
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
