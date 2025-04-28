package com.eroom.mail.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
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

	private final MailRepository mailRepository;
	private final MailReceiverRepository mailReceiverRepository;
	private final EmployeeRepository employeeRepository;
	
	private final DriveRepository driveRepository;
	@Value("${ffupload.location}")
	 private String fileDir;
	
	// 휴지통 N > Y 업데이트 
	public void moveToTrash(Long employeeNo, Long id) {
	    mailReceiverRepository.updateDeletedYnAndTime(employeeNo, id);
	}
	
	@Transactional
	public void deleteReceivedMailById(Long employeeNo,Long mailReceiverNo) {
	    mailReceiverRepository.deleteByIdAndEmployeeNo(employeeNo, mailReceiverNo);
	}
	
	// 디테일 조회할때 읽음 컬럼 N => Y로 업데이트
	@Transactional
	public Mail selectMailOne(Long employeeNo,Long id) {
		
	    mailReceiverRepository.updateReadYn(employeeNo,id); // 읽음 처리
		return mailRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("메일을 찾을 수 없습니다."));
	}
	// 휴지통 조회하는곳
	public List<MailReceiver> getTrashMailsByEmployee(Long employeeNo, String sortOrder) {
	    List<MailReceiver> resultList;

	    if ("latest".equals(sortOrder)) {
	        resultList = mailReceiverRepository.findByEmployeeNoOrderByLatest(employeeNo);
	    } else if ("oldest".equals(sortOrder)) {
	        resultList = mailReceiverRepository.findByEmployeeNoOrderByOldest(employeeNo);
	    } else {
	        throw new IllegalArgumentException("유효하지 않은 정렬 조건: " + sortOrder);
	    }

	    return filterOnlyDeletedMails(resultList);
	}
	
	// 임시저장 여부 확인 메소드
	private List<Mail> filterNotDraftMails(List<Mail> mails) {
	    List<Mail> result = new ArrayList<>();
	    for (Mail mail : mails) {
	        if (!"Y".equals(mail.getMailStatus())) { // mail_status가 Y면 임시저장
	            result.add(mail);
	        }
	    }
	    return result;
	}
	
	// 휴지통 여부 로직 메소드
	public List<MailReceiver> filterNotDeletedMails(List<MailReceiver> mails) {
	    List<MailReceiver> result = new ArrayList<>();
	    for (MailReceiver mail : mails) {
	        if (!"Y".equals(mail.getMailReceiverDeletedYn())) {
	            result.add(mail);
	        }
	    }
	    return result;
	}
	
	
	// 휴지통조회 Y인 메일만 조회
	private List<MailReceiver> filterOnlyDeletedMails(List<MailReceiver> mails) {
	    List<MailReceiver> result = new ArrayList<>();
	    for (MailReceiver mail : mails) {
	        if ("Y".equals(mail.getMailReceiverDeletedYn())) {
	            result.add(mail);
	        }
	    }
	    return result;
	}
	
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
		// 휴지통 Y여부 확인해서 걸러줌
		return filterNotDeletedMails(resultList);
	}
	
    // 본인 메일 조회
//	public List<Mail> findMailsBySender(Long employeeNo) {
//	    return mailRepository.findBySenderEmployeeNo(employeeNo);
//	}
	
	// 지금은 최신순
	// 조건줘서 최신 > 오래된 변환하게 
	// 임시 저장도 넣어야함
	public List<Mail> findMailsBySender(Long employeeNo,String sortOrder) {
		List<Mail> resultList = null;
		
		if(sortOrder.equals("latest")) {
	     resultList= mailRepository.findBySenderEmployeeNoOrderByMailSentTimeDesc(employeeNo);
		}else if(sortOrder.equals("oldest")) {
			 resultList= mailRepository.findBySenderEmployeeNoOrderByMailSentTimeAsc(employeeNo);
		}
		return filterNotDraftMails(resultList);
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
	
	// 임시 저장
	public int saveMail(MailDto mailDto, String mailStatus) {
		int result = 0;
		try {
			mailDto.setMail_status(mailStatus);
			Mail mailEntity = mailDto.toEntity();
			Mail mailSaver = mailRepository.save(mailEntity);
			result = 1;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 메일 생성
	@Transactional
	public int createMail(MailDto mailDto,List<MultipartFile> mailFiles ) {
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
		            
		            MailReceiverDto mailReceiverDto = new MailReceiverDto();
		            mailReceiverDto.setEmployee_no(receiverNo);
		            mailReceiverDto.setMail_no(mailSaver.getMailNo());

		            MailReceiver mailReceiver = mailReceiverDto.toEntity(mailSaver, receiver);
		            mailReceiverRepository.save(mailReceiver);

		        }
		        for (MultipartFile file : mailFiles) {
		            if (!file.isEmpty()) {
		                // DriveDto 생성
		                String oriName = file.getOriginalFilename();
				        String ext = oriName.substring(oriName.lastIndexOf("."));
				        String newName = UUID.randomUUID().toString().replace("-", "") + ext;
				        String path = fileDir + "personal/" + newName;
		                DriveDto driveDto = new DriveDto();
		                
		                File savedFile = new File(path);
				        if (!savedFile.getParentFile().exists()) {
				            savedFile.getParentFile().mkdirs();
				        }
				        file.transferTo(savedFile);
				        
				        
		                driveDto.setDriveOriName(oriName);
		                driveDto.setDriveSize(file.getSize());
		                
		                
		               
		                // Drive 엔티티로 변환
		                Drive drive = new Drive();
		                drive.setDriveOriName(driveDto.getDriveOriName());
		                drive.setDriveNewName(newName); // 파일 고유 이름 생성
		                drive.setDriveSize(driveDto.getDriveSize());
		                drive.setDriveType(ext);
		                drive.setDrivePath("/upload/mail/" + newName); // 실제 저장 경로로 변경 필요
		                drive.setUploader(Employee.builder().employeeNo(mailDto.getEmployee_no()).build());
		                drive.setParam1(mailSaver.getMailNo()); // 메일 참조 연결
		                drive.setSeparatorCode("FL002");
		                drive.setVisibleYn("Y");
		                // DB에 저장
		                driveRepository.save(drive);
		                
		            }
		        }
		        
				/*
				 * for (MultipartFile file : mailFiles) { if (!file.isEmpty()) { Drive drive =
				 * new Drive(); drive.setDriveOriName(file.getOriginalFilename());
				 * drive.setDriveNewName(UUID.randomUUID().toString());
				 * drive.setDriveType(FilenameUtils.getExtension(file.getOriginalFilename()));
				 * drive.setDriveSize(file.getSize()); drive.setDrivePath("저장경로"); // 네 프로젝트
				 * 기준으로 수정
				 * drive.setUploader(Employee.builder().employeeNo(mailDto.getEmployee_no()).
				 * build()); drive.setPrm1(mailSaver.getMailNo()); // 메일 참조 연결!
				 * 
				 * driveRepository.save(drive); } }
				 */
			
			
			
			
			
			
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
