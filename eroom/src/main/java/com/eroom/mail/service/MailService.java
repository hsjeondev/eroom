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
	
	public String getPreviewContent(Mail mail) {
	    String content = mail.getMailContent();
	    if (content == null) return "";

	    String plainText = content.replaceAll("<[^>]*>", ""); // HTML 태그 제거
	    return plainText.length() > 30 ? plainText.substring(0, 30) + "..." : plainText;
	}
	
	private List<MailReceiver> filterOnlyToStatusMails(List<MailReceiver> mails) {
	    List<MailReceiver> result = new ArrayList<>();
	    for (MailReceiver mail : mails) {
	        if ("To".equals(mail.getMailReceiverType())) {  // "To" 상태 필터링
	            result.add(mail);
	        }
	    }
	    return result;
	}
	
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
	
	// 다운로드 파일 정보 
	public Drive findAttachmentById(Long driveId) {
	    return driveRepository.findById(driveId)
	            .orElse(null);
	}
	
	// 디테일에서 파일 조회
	public List<Drive> findMailAttachments(Long mailId) {
	    return driveRepository.findBySeparatorCodeAndParam1AndVisibleYn("FL002", mailId, "Y");
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
	
	
	public List<Mail> findOnlyDraftMailsBySender(Long employeeNo, String sortOrder) {
	    List<Mail> resultList = null;

	    if ("latest".equals(sortOrder)) {
	        resultList = mailRepository.findBySenderEmployeeNoOrderByMailSentTimeDesc(employeeNo);
	    } else if ("oldest".equals(sortOrder)) {
	        resultList = mailRepository.findBySenderEmployeeNoOrderByMailSentTimeAsc(employeeNo);
	    } else {
	        throw new IllegalArgumentException("정렬 조건이 올바르지 않습니다: " + sortOrder);
	    }

	    // 임시저장만 필터
	    List<Mail> filteredDrafts = filterOnlyDraftMails(resultList);

	    // 미리보기 내용 설정
	    for (Mail mail : filteredDrafts) {
	        String preview = getPreviewContent(mail);
	        mail.setMailContent(preview);
	    }

	    return filteredDrafts;
	}
	// 임시 저장 불러오기중
	public Mail getMailById(Long mailNo) {
        return mailRepository.findById(mailNo)
                .orElseThrow(() -> new RuntimeException("Mail not found with id: " + mailNo));
    }
	
	// 임시저장된 메일 조회 메소드 
		private List<Mail> filterOnlyDraftMails(List<Mail> mails) {
		    List<Mail> result = new ArrayList<>();
		    for (Mail mail : mails) {
		        if ("Y".equals(mail.getMailStatus())) { // mail_status가 Y면 임시저장
		            result.add(mail);
		        }
		    }
		    return result;
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
	
	  /*public List<Mail> findMailsBySender(Long employeeNo,String sortOrder) {
		List<Mail> resultList = null;
		
		if(sortOrder.equals("latest")) {
	     resultList= mailRepository.findBySenderEmployeeNoOrderByMailSentTimeDesc(employeeNo);
		}else if(sortOrder.equals("oldest")) {
			 resultList= mailRepository.findBySenderEmployeeNoOrderByMailSentTimeAsc(employeeNo);
		}
		return filterNotDraftMails(resultList);
	}*/
	public List<Mail> findMailsBySender(Long employeeNo, String sortOrder) {
	    List<Mail> resultList = null;

	    if ("latest".equals(sortOrder)) {
	        resultList = mailRepository.findBySenderEmployeeNoOrderByMailSentTimeDesc(employeeNo);
	    } else if ("oldest".equals(sortOrder)) {
	        resultList = mailRepository.findBySenderEmployeeNoOrderByMailSentTimeAsc(employeeNo);
	    }
	    // 휴지통 여부도 해야함
	    // 중요 여부도 해야함
	    // 사용 여부도 해야함 
	    
	    // 임시저장 여부
	    List<Mail> filtered = filterNotDraftMails(resultList);
	    

	    // HTML 태그 제거한 값으로 mailContent 직접 덮어쓰기
	    for (Mail mail : filtered) {
	        String preview = getPreviewContent(mail); // 이 함수 안에서 태그 제거함
	        mail.setMailContent(preview); // 덮어쓰기!
	    }

	    return filtered;
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
			
			
			
			// 수신자
			 List<Long> receiverNos = mailDto.getReceiverNos();
		        for (Long receiverNo : receiverNos) {
		            // FK 관계 맞춰줌
		            Employee receiver = employeeRepository.findById(receiverNo).orElseThrow(() -> 
		                new IllegalArgumentException("존재하지 않는 사원 번호: " + receiverNo));
		            
		            MailReceiverDto mailReceiverDto = new MailReceiverDto();
		            mailReceiverDto.setEmployee_no(receiverNo);
		            mailReceiverDto.setMail_recervier_type("To");
		            mailReceiverDto.setMail_no(mailSaver.getMailNo());

		            MailReceiver mailReceiver = mailReceiverDto.toEntity(mailSaver, receiver);
		            mailReceiverRepository.save(mailReceiver);

		        }
		        // 참조자
		        List<Long> ccNos = mailDto.getCcNos(); // 참조자 리스트
		        if (ccNos != null) {
		            for (Long ccNo : ccNos) {
		                Employee ccReceiver = employeeRepository.findById(ccNo).orElseThrow(() ->
		                    new IllegalArgumentException("존재하지 않는 사원 번호: " + ccNo));

		                MailReceiverDto ccReceiverDto = new MailReceiverDto();
		                ccReceiverDto.setEmployee_no(ccNo);
		                ccReceiverDto.setMail_no(mailSaver.getMailNo());
		                ccReceiverDto.setMail_recervier_type("Cc");
		                MailReceiver mailReceiver = ccReceiverDto.toEntity(mailSaver, ccReceiver);
		                mailReceiverRepository.save(mailReceiver);
		            }
		        }
		        // 파일
		        for (MultipartFile file : mailFiles) {
		            if (!file.isEmpty()) {
		                // DriveDto 생성
		                String oriName = file.getOriginalFilename();
				        String ext = oriName.substring(oriName.lastIndexOf("."));
				        String newName = UUID.randomUUID().toString().replace("-", "") + ext;
				        String path = fileDir + "mail/" + newName;
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
		                drive.setDrivePath("/mail/" + newName); // 실제 저장 경로로 변경 필요
		                drive.setUploader(Employee.builder().employeeNo(mailDto.getEmployee_no()).build());
		                drive.setParam1(mailSaver.getMailNo()); // 메일 참조 연결
		                drive.setSeparatorCode("FL002");
		                drive.setVisibleYn("Y");
		                // DB에 저장
		                driveRepository.save(drive);
		                
		            }
		        }
		     // 본인
		        
				Employee sender = employeeRepository.findById(mailDto.getEmployee_no())
		                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원 번호: " + mailDto.getEmployee_no()));

		        MailReceiverDto selfReceiverDto = new MailReceiverDto();
		        selfReceiverDto.setEmployee_no(sender.getEmployeeNo());
		        selfReceiverDto.setMail_no(mailSaver.getMailNo());
		        selfReceiverDto.setMail_recervier_type("Me");
		        MailReceiver selfReceiver = selfReceiverDto.toEntity(mailSaver, sender);
		        mailReceiverRepository.save(selfReceiver);
		        

			result =1;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
