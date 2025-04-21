package com.eroom.mail.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	private final MailRepository mailRepository;
	private final MailReceiverRepository mailReceiverRepository;
	private final EmployeeRepository employeeRepository;

	
	
	// 받은 메일 조회
	/*public List<MailReceiver> getReceivedMailsByEmployee(Long employeeNo) {
        return mailReceiverRepository.findByEmployeeNo(employeeNo);
    }*/
	public List<MailReceiver> getReceivedMailsByEmployee(Long employeeNo, String sortOrder) {
			List<MailReceiver> resultList = null;
		
		if(sortOrder.equals("latest")) {
	     resultList= mailReceiverRepository.findByEmployeeNoOrderByLatest(employeeNo);
		}else if(sortOrder.equals("oldest")) {
			 resultList= mailReceiverRepository.findByEmployeeNoOrderByOldest(employeeNo);
		}
		return resultList;
	}
	
    // 본인 메일 조회
//	public List<Mail> findMailsBySender(Long employeeNo) {
//	    return mailRepository.findBySenderEmployeeNo(employeeNo);
//	}
	
	// 지금은 최신순
	// 조건줘서 최신 > 오래된 변환하게 
	public List<Mail> findMailsBySender(Long employeeNo,String sortOrder) {
		List<Mail> resultList = null;
		
		if(sortOrder.equals("latest")) {
	     resultList= mailRepository.findBySenderEmployeeNoOrderByMailSentTimeDesc(employeeNo);
		}else if(sortOrder.equals("oldest")) {
			 resultList= mailRepository.findBySenderEmployeeNoOrderByMailSentTimeAsc(employeeNo);
		}
		return resultList;
	}

	// 지금 쓰는곳 없음 삭제 해도 될듯
	public List<Mail> selectMailAll(){
		List<Mail> list = mailRepository.findAll();
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
			Mail mailSaver = mailRepository.save(mailEntity);
			
			
			 List<Long> receiverNos = mailDto.getReceiverNos();
		        for (Long receiverNo : receiverNos) {
		            // FK 관계 맞춰줌
		            Employee receiver = employeeRepository.findById(receiverNo).orElseThrow(() -> 
		                new IllegalArgumentException("존재하지 않는 사원 번호: " + receiverNo));
		            

		            
		            MailReceiver mailReceiver = MailReceiver.builder()
		                    .mail(mailSaver) // 발송된 메일
		                    .receiver(receiver) // 수신자 (Employee)
		                    .build();
		            mailReceiverRepository.save(mailReceiver);
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
