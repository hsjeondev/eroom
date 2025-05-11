package com.eroom.mail.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.eroom.drive.dto.DriveDto;
import com.eroom.drive.entity.Drive;
import com.eroom.drive.repository.DriveRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.mail.dto.MailAlarmDto;
import com.eroom.mail.dto.MailDraftDto;
import com.eroom.mail.dto.MailDto;
import com.eroom.mail.dto.MailReceiverDto;
import com.eroom.mail.dto.MailStatusDto;
import com.eroom.mail.entity.Mail;
import com.eroom.mail.entity.MailAlarm;
import com.eroom.mail.entity.MailDraft;
import com.eroom.mail.entity.MailReceiver;
import com.eroom.mail.entity.MailStatus;
import com.eroom.mail.repository.MailAlarmRepository;
import com.eroom.mail.repository.MailDraftRepository;
import com.eroom.mail.repository.MailReceiverRepository;
import com.eroom.mail.repository.MailRepository;
import com.eroom.mail.repository.MailStatusRepository;
import com.eroom.notification.entity.Alarm;
import com.eroom.notification.repository.AlarmRepository;
import com.eroom.websocket.MailAlarmWebSocketHandler;
import com.eroom.websocket.MailWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

	private final MailRepository mailRepository;
	private final MailReceiverRepository mailReceiverRepository;
	private final EmployeeRepository employeeRepository;
	private final MailStatusRepository mailStatusRepository;
	private final MailDraftRepository mailDraftRepository;
	private final DriveRepository driveRepository;
	private final MailAlarmRepository mailAlarmRepository;
	
	private final AlarmRepository alarmRepository;
	
	private final MailAlarmWebSocketHandler mailAlarmWebSocketHandler;
	private final MailWebSocketHandler mailWebSocketHandler;
	@Value("${ffupload.location}")
	 private String fileDir;
	
	public String getPreviewContent(Mail mail) {
	    String content = mail.getMailContent();
	    if (content == null) return "";

	    String plainText = content.replaceAll("<[^>]*>", ""); // HTML 태그 제거
	    return plainText.length() > 30 ? plainText.substring(0, 30) + "..." : plainText;
	}
	
	// 홈 화면 카드 읽지 않은 메일/ 받은 메일
	public int countUnreadMails(Long employeeNo) {
	    return mailReceiverRepository.countByReceiverEmployeeNoAndMailReceiverReadYn(employeeNo, "N");
	}

	public int countAllReceivedMails(Long employeeNo) {
	    return mailReceiverRepository.countByReceiverEmployeeNo(employeeNo);
	}
	
	// AlarmService 알람 테이블 메소드
	public MailAlarm findAlarmOne(Long mailAlarmNo) {
	    return mailAlarmRepository.findById(mailAlarmNo).orElse(null);
	}
	
	// 주소록에서 메일쓰기
	public Employee getEmployeeByNo(Long employeeNo) {
		
	    // Long senderEmpNo = mailRepository.findSenderEmployeeNoByMailNo(mailNo);
	    return employeeRepository.findById(employeeNo)
	        .orElseThrow(() -> new RuntimeException("작성자 정보 없음"));
	}
	
	// 답장 작성 페이지 이름 넘기려고 쓰는 메소드
	public Employee getSenderInfoByMailNo(Long mailNo) {
	    Long senderEmpNo = mailRepository.findSenderEmployeeNoByMailNo(mailNo);
	    return employeeRepository.findById(senderEmpNo)
	        .orElseThrow(() -> new RuntimeException("작성자 정보 없음"));
	}
	// 임시저장 파일 조회
	public List<Drive> findFilesByMailNo(Long mailNo) {
	    return driveRepository.findBySeparatorCodeAndParam1AndVisibleYn("FL002", mailNo, "Y");
	}
	
	// 메일no로 임시 저장 조회
	public List<MailDraft> findReceiversByMailNo(Long mailNo) {
	    return mailDraftRepository.findByMailNoInMail(mailNo);
	}
	
	
	
	
	// 메일no로 메일 정보 조회
	public Mail findMailByMailNo(Long mailNo) {
	    return mailRepository.findById(mailNo)
	            .orElseThrow(() -> new RuntimeException("메일을 찾을 수 없습니다."));
	}
	
	@Transactional
	 public void markAsDeleted(Long attachNo) {
	     Drive drive = driveRepository.findById(attachNo)
	             .orElseThrow(() -> new IllegalArgumentException("해당 첨부파일을 찾을 수 없습니다."));

	     drive.setVisibleYn("N");
	     driveRepository.save(drive); 
	 }
	
	
	
	
	
	//폐기 예정
//	private List<MailReceiver> filterOnlyToStatusMails(List<MailReceiver> mails) {
//	    List<MailReceiver> result = new ArrayList<>();
//	    for (MailReceiver mail : mails) {
//	        if ("To".equals(mail.getMailReceiverType())) {  // "To" 상태 필터링
//	            result.add(mail);
//	        }
//	    }
//	    return result;
//	}
	
	// 휴지통 N > Y 업데이트 
//	public void moveToTrash(Long employeeNo, Long id) {
//	    mailStatusRepository.updateDeletedYnAndTime(employeeNo, id);
//	}
	/*
	@Transactional
	public void deleteReceivedMailById(Long employeeNo,Long mailReceiverNo) {
	    mailReceiverRepository.deleteByIdAndEmployeeNo(employeeNo, mailReceiverNo);
	}
	
	// 디테일 조회할때 읽음 컬럼 N => Y로 업데이트*/
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
	// 테이블 바뀐 뒤 주석 처리
	/*
	public List<Mail> getTrashMailsByEmployee(Long employeeNo, String sortOrder) {
	    List<Mail> resultList=null;

	    if ("latest".equals(sortOrder)) {
	        resultList = mailRepository.findTrashMailsByEmployeeNo(employeeNo);
	    } else if ("oldest".equals(sortOrder)) {
	        resultList = mailRepository.findTrashMailsByEmployeeNoOldest(employeeNo);
	    } else {
	        throw new IllegalArgumentException("유효하지 않은 정렬 조건: " + sortOrder);
	    }

	    return resultList;
	}
	*/
	public List<Mail> getTrashMailsByEmployee(Long employeeNo, String sortOrder) {
	    if ("latest".equalsIgnoreCase(sortOrder)) {
	        return mailStatusRepository.findAllDeletedMailsByEmployeeLatest(employeeNo);
	    } else if ("oldest".equalsIgnoreCase(sortOrder)) {
	        return mailStatusRepository.findAllDeletedMailsByEmployeeOldest(employeeNo);
	    } else {
	        throw new IllegalArgumentException("유효하지 않은 정렬 조건: " + sortOrder);
	    }
	}
	
	// 중요 메일 조회 
	public List<Mail> getImportantMailsByEmployee(Long employeeNo, String sortOrder) {
	    if ("latest".equalsIgnoreCase(sortOrder)) {
	        return mailStatusRepository.findAllImportantMailsByEmployeeLatest(employeeNo);
	    } else if ("oldest".equalsIgnoreCase(sortOrder)) {
	        return mailStatusRepository.findAllImportantMailsByEmployeeOldest(employeeNo);
	    } else {
	        throw new IllegalArgumentException("유효하지 않은 정렬 조건: " + sortOrder);
	    }
	}
	/*테이블 교체후 오류 수정중
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
	*/
	// 임시 저장 불러오기중
	public Mail getMailById(Long mailNo) {
        return mailRepository.findById(mailNo)
                .orElseThrow(() -> new RuntimeException("Mail not found with id: " + mailNo));
    }
	
	// 임시저장된 메일 조회 메소드 
	/*테이블 교체후 오류 수정중
		private List<Mail> filterOnlyDraftMails(List<Mail> mails) {
		    List<Mail> result = new ArrayList<>();
		    for (Mail mail : mails) {
		        if ("Y".equals(mail.getMailStatus())) { // mail_status가 Y면 임시저장
		            result.add(mail);
		        }
		    }
		    return result;
		}
	*/
	// 임시저장 여부 확인 메소드
	/* 테이블 교체후 오류 수정중
	private List<Mail> filterNotDraftMails(List<Mail> mails) {
	    List<Mail> result = new ArrayList<>();
	    for (Mail mail : mails) {
	        if (!"Y".equals(mail.getMailStatus())) { // mail_status가 Y면 임시저장
	            result.add(mail);
	        }
	    }
	    return result;
	}
	*/
	// 휴지통 여부 로직 메소드
	/*테이블 바뀐 뒤 주석 처리
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
	*/
	
	// 받은 메일 조회
	/*public List<MailReceiver> getReceivedMailsByEmployee(Long employeeNo) {
        return mailReceiverRepository.findByEmployeeNo(employeeNo);
    }
    테이블 바뀐 뒤 주석 처리
    */
	public List<MailReceiver> getReceivedMailsByEmployee(Long employeeNo, String sortOrder) {
			List<MailReceiver> resultList = null;
		
		if(sortOrder.equals("latest")) {
	     resultList= mailReceiverRepository.findByEmployeeNoOrderByLatest(employeeNo);
		}else if(sortOrder.equals("oldest")) {
			 resultList= mailReceiverRepository.findByEmployeeNoOrderByOldest(employeeNo);
		}
		// 휴지통 Y여부 확인해서 걸러줌
		
		return resultList;
	}
	// 휴지통에서 삭제
	@Transactional
	public void updateVisibleYn(Long employeeNo, Long mailNo, String visibleYn) {
        mailStatusRepository.updateVisibleYn(employeeNo, mailNo, visibleYn);
    }
	// 체크 박스로 처리하는 별 표시 ( 중요 표시 )
	@Transactional
	public void toggleStarred(Long mailNo, Long employeeNo) {
	    Optional<MailStatus> existingStatus = mailStatusRepository.findByMail_mailNoAndEmployee_employeeNo(mailNo, employeeNo);

	    if (existingStatus.isPresent()) {
	    	
	        MailStatus status = existingStatus.get();
	        status.setMailStatusDeletedTime(null);
	        status.setMailStatusDeletedYn("N");
	        
	        // 별표 상태가 "Y"이면 그대로 두고, "N"이면 "Y"로 변경
	        if ("N".equals(status.getMailStatusImportantYn())) {
	            status.setMailStatusImportantYn("Y");
	        }
	        
	        mailStatusRepository.save(status); // 상태 저장
	    } else {
	        // 상태가 없으면 새로 상태 추가
	        MailStatusDto mailStatusDto = new MailStatusDto();
	        mailStatusDto.setMail_no(mailNo);
	        mailStatusDto.setEmployee_no(employeeNo);
	        mailStatusDto.setMail_status_important_yn("Y"); // 별표 상태는 "Y"로 설정
	        //mailStatusDto.setMailStatusDeletedTime(LocalDateTime.now());
	        
	        MailStatus mailStatusEntity = mailStatusDto.toEntity();
	        //mailStatusEntity.setMailStatusDeletedTime(LocalDateTime.now());
	        mailStatusRepository.save(mailStatusEntity);
	    }
	}

	// 별 단일 처리(중요 표시)
	@Transactional
	public void moveImportant(Long mailNo, Long employeeNo) {
        Optional<MailStatus> existingStatus = mailStatusRepository.findByMail_mailNoAndEmployee_employeeNo(mailNo, employeeNo);

        if (existingStatus.isPresent()) {
            // 상태가 이미 존재하면 중요 표시 여부를 반전시킴
            MailStatus status = existingStatus.get();
            status.setMailStatusDeletedTime(null);
            status.setMailStatusDeletedYn("N");
            status.setMailStatusImportantYn(status.getMailStatusImportantYn().equals("Y") ? "N" : "Y"); // "Y" -> "N", "N" -> "Y"로 토글
            mailStatusRepository.save(status); // 상태 저장
        } else {
            // 상태가 없으면 새로 상태 추가
        	MailStatusDto mailStatusDto = new MailStatusDto();
	        mailStatusDto.setMail_no(mailNo);
	        mailStatusDto.setEmployee_no(employeeNo);
	        mailStatusDto.setMail_status_important_yn("Y");
	        //mailStatusDto.setMail_status_deleted_time(LocalDateTime.now());
	        MailStatus mailStatusEntity = mailStatusDto.toEntity();
	        //mailStatusEntity.setMailStatusDeletedTime(LocalDateTime.now());
	        mailStatusRepository.save(mailStatusEntity);
        }
    }
	public Map<Long, MailStatus> getStatusMapForMailRecevier(List<MailReceiver> mails) {
	    List<Long> mailNos = mails.stream()
	                              .map(mr -> mr.getMail().getMailNo())
	                              .collect(Collectors.toList());

	    List<MailStatus> statusList = mailStatusRepository.findByMailMailNoIn(mailNos);

	    return statusList.stream()
	            .collect(Collectors.toMap(ms -> ms.getMail().getMailNo(), Function.identity()));
	}
	
	public Map<Long, MailStatus> getStatusMapForMails(List<Mail> mails) {
	    List<Long> mailNos = mails.stream()
	                              .map(Mail::getMailNo)
	                              .collect(Collectors.toList());

	    List<MailStatus> statusList = mailStatusRepository.findByMailMailNoIn(mailNos);

	    return statusList.stream()
	            .collect(Collectors.toMap(ms -> ms.getMail().getMailNo(), Function.identity()));
	}
	
	@Transactional
	public void moveToTrash(Long employeeNo, Long mailNo) {
	    Optional<MailStatus> optionalStatus = mailStatusRepository.findByMail_mailNoAndEmployee_employeeNo(mailNo, employeeNo);

	    MailStatus status;
	    if (optionalStatus.isPresent()) {
	        status = optionalStatus.get();
	        status.setMailStatusImportantYn("N");
	        status.setMailStatusDeletedTime(LocalDateTime.now());
	        status.setMailStatusDeletedYn(status.getMailStatusDeletedYn().equals("Y") ? "N" : "Y"); // "Y" -> "N", "N" -> "Y"로 토글
            mailStatusRepository.save(status); // 상태 저장
	    } else {
	        // 없으면 새로 생성
        	MailStatusDto mailStatusDto = new MailStatusDto();
	        mailStatusDto.setMail_no(mailNo);
	        mailStatusDto.setEmployee_no(employeeNo);
	        mailStatusDto.setMail_status_deleted_yn("Y");
	        //mailStatusDto.setMail_status_deleted_time(LocalDateTime.now());
	        MailStatus mailStatusEntity = mailStatusDto.toEntity();
	        mailStatusEntity.setMailStatusDeletedTime(LocalDateTime.now());
	        mailStatusRepository.save(mailStatusEntity);

	    	
//	        status = MailStatus.builder()
//	                .mail(mailRepository.findById(mailNo).orElseThrow())
//	                .employee(employeeRepository.findById(employeeNo).orElseThrow())
//	                .build();
//	        status.setMailStatusDeletedYn("Y");
//	        status.setMailStatusDeletedTime(LocalDateTime.now());
//	        mailStatusRepository.save(status);
	    }

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
	        resultList = mailRepository.findSentMailsLatest(employeeNo);
	    } else if ("oldest".equals(sortOrder)) {
	        resultList = mailRepository.findSentMailsOldest(employeeNo);
	    }
	    // 휴지통 여부도 해야함
	    // 중요 여부도 해야함
	    // 사용 여부도 해야함 
	    
	    // 임시저장 여부
	    //List<Mail> filtered = filterNotDraftMails(resultList);
	    

	    // HTML 태그 제거한 값으로 mailContent 직접 덮어쓰기
	    /*
	    for (Mail mail : filtered) {
	        String preview = getPreviewContent(mail); // 이 함수 안에서 태그 제거함
	        mail.setMailContent(preview); // 덮어쓰기!
	    }
	    */

	    //return filtered;
	    return resultList;
	}
	// 임시 저장 조회
	public List<Mail> findDraftsByEmployee(Long employeeNo, String sortOrder) {
	    if ("latest".equals(sortOrder)) {
	        return mailRepository.findDraftMailsLatest(employeeNo);
	    } else if ("oldest".equals(sortOrder)) {
	        return mailRepository.findDraftMailsOldest(employeeNo);
	    } else {
	        // 기본은 최신순
	        return mailRepository.findDraftMailsLatest(employeeNo);
	    }
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
	
	// 임시 저장 폐기 예정
	public int saveMail(MailDto mailDto, String mailStatus) {
		int result = 0;
		try {
			//mailDto.setMail_status(mailStatus);
			Mail mailEntity = mailDto.toEntity();
			Mail mailSaver = mailRepository.save(mailEntity);
			result = 1;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 메일 생성
	// 메일 발송
	@Transactional
	public int createMail(MailDto mailDto,List<MultipartFile> mailFiles, String mailDraftYn) {
		int result = 0;
		List<Long> receiverNos = new ArrayList<>();
		try {
			// 보낸 메일 저장 ( mail에 제목, 내용 저장)
			Mail mailEntity = mailDto.toEntity();
			mailEntity.setMailSentTime(LocalDateTime.now());
			Mail mailSaver = mailRepository.save(mailEntity);
			
			String receiverType = mailDto.getReceiver_type(); 
			
			if ("root".equals(receiverType)) {
				List<Employee> allEmployees = employeeRepository.findAll();
			    for (Employee e : allEmployees) {
			        receiverNos.add(e.getEmployeeNo());
			    }
			}  else if (receiverType.startsWith("D")) {
			    // 부서 코드
			    List<Employee> deptEmployees = employeeRepository.findByStructureParentCode(receiverType);
			    for (Employee e : deptEmployees) {
			        receiverNos.add(e.getEmployeeNo());
			    }
			} else if (receiverType.startsWith("T")) {
			    // 팀 ID
			    List<Employee> teamEmployees = employeeRepository.findByStructure_SeparatorCode(receiverType);
			    for (Employee e : teamEmployees) {
			        receiverNos.add(e.getEmployeeNo());
			    }
			}else {
				String[] empNoArray = receiverType.split(",");
			    for (String empNoStr : empNoArray) {
			        try {
			            Long empNo = Long.parseLong(empNoStr.trim());
			            receiverNos.add(empNo);
			        } catch (NumberFormatException e) {
			            throw new IllegalArgumentException("잘못된 직원 번호입니다: " + empNoStr);
			        }
			    }
			}
			
			// 부서
			/*List<Employee> receiverEmployees = employeeRepository.findByStructureParentCode(mailDto.getReceiver_type());
			for(Employee re : receiverEmployees) {
				
				receiverNos.add(re.getEmployeeNo());
			}*/
			
			//List<Long> receiverNos = mailDto.getReceiver_no();
			if(mailDraftYn.equals("N")) {
			// 수신자 ( mail_receiver 받는 사람 mail_no값으로 저장 )
			 if (receiverNos != null && !receiverNos.isEmpty()) {
		        for (Long receiverNo : receiverNos) {
		            // FK 관계 맞춰줌
		            Employee receiver = employeeRepository.findById(receiverNo).orElseThrow(() -> 
		                new IllegalArgumentException("존재하지 않는 사원 번호: " + receiverNo));
		            
		            MailReceiverDto mailReceiverDto = new MailReceiverDto();
		            
		            mailReceiverDto.setEmployee_no(receiver.getEmployeeNo());
		            
		            mailReceiverDto.setMail_no(mailSaver.getMailNo());

		            MailReceiver mailReceiverEntity = mailReceiverDto.toEntity();
		            MailReceiver mailRecieverSaver=mailReceiverRepository.save(mailReceiverEntity);
		            
		            MailAlarmDto mailAlarmDto = new MailAlarmDto();
		            mailAlarmDto.setMail_receiver_no(mailRecieverSaver.getMailReceiverNo());
	            	MailAlarm mailAlarm = mailAlarmDto.toEntity();
	            	MailAlarm savedMailAlarm =mailAlarmRepository.save(mailAlarm);
	            	
	            	
	            	Alarm alarm = Alarm.builder()
	            			.employeeNo(mailRecieverSaver.getReceiver().getEmployeeNo())
	            		    .separatorCode("R003")                    // 메일 알림 코드
	            		    .readYn("N")
	            		    .param1(savedMailAlarm.getMailAlarmNo())  // mail_alarm_no 저장
	            		    .regDate(LocalDateTime.now())
	            		    .build();

	            	alarmRepository.save(alarm);
	            	
	            	/*
	            	.alarm_no(alarm.getAlarmNo())
					// 메일 알람 PK
					.param1(alarm.getParam1())
					// 메일 알람 코드
					.separator_code(alarm.getSeparatorCode())
					// 메일 받은 사람 PK
					.employee_no(alarm.getEmployeeNo())
					// 읽음
					.read_yn(alarm.getReadYn())
					// 날짜
					.reg_date(alarm.getRegDate())
					.build();
	            	*/
	            	
		         /*
		         try {
		             Map<String, Object> message = new HashMap<>();
		             message.put("type", "mail");
		             message.put("mailTitle", mailDto.getMail_title());
		             message.put("senderName", mailSaver.getSender().getEmployeeName());
		             message.put("mailNo", mailSaver.getMailNo());

		             String jsonMessage = new ObjectMapper().writeValueAsString(message);
		             mailWebSocketHandler.sendToUser(receiverNo, jsonMessage);
		         } catch (Exception e) {
		             e.printStackTrace(); // 웹소켓 전송 실패해도 메일 저장은 정상 진행
		         }
		         */
	            	try {
                        Map<String, Object> message = new HashMap<>();
                        message.put("type", "mail");
                        message.put("mailTitle", mailDto.getMail_title());
                        message.put("senderName", mailSaver.getSender().getEmployeeName());
                        message.put("mailNo", mailSaver.getMailNo());

                        // 메시지 JSON으로 변환
                        String jsonMessage = new ObjectMapper().writeValueAsString(message);

                        // WebSocket을 통해 알림 전송
                        mailWebSocketHandler.sendToUser(receiverNo, jsonMessage);
                    } catch (Exception e) {
                        e.printStackTrace(); // 웹소켓 전송 실패해도 메일 저장은 정상 진행
                    }
		         // 발송된 메일만 파일 저장
					/*
					 * for (MultipartFile file : mailFiles) { if (!file.isEmpty()) { // DriveDto 생성
					 * String oriName = file.getOriginalFilename(); String ext =
					 * oriName.substring(oriName.lastIndexOf(".")); String newName =
					 * UUID.randomUUID().toString().replace("-", "") + ext; String path = fileDir +
					 * "mail/" + newName; DriveDto driveDto = new DriveDto();
					 * 
					 * File savedFile = new File(path); if (!savedFile.getParentFile().exists()) {
					 * savedFile.getParentFile().mkdirs(); } file.transferTo(savedFile);
					 * 
					 * 
					 * driveDto.setDriveOriName(oriName); driveDto.setDriveSize(file.getSize());
					 * 
					 * 
					 * 
					 * // Drive 엔티티로 변환 Drive drive = new Drive();
					 * drive.setDriveOriName(driveDto.getDriveOriName());
					 * drive.setDriveNewName(newName); // 파일 고유 이름 생성
					 * drive.setDriveSize(driveDto.getDriveSize()); drive.setDriveType(ext);
					 * drive.setDrivePath("mail/" + newName); // 실제 저장 경로로 변경 필요
					 * drive.setUploader(Employee.builder().employeeNo(mailDto.getEmployee_no()).
					 * build()); drive.setParam1(mailSaver.getMailNo()); // 메일 참조 연결
					 * drive.setSeparatorCode("FL002"); drive.setVisibleYn("Y"); // DB에 저장
					 * driveRepository.save(drive);
					 * 
					 * } }
					 */
		            
		        }
			 }
			}
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
	                drive.setDrivePath("mail/" + newName); // 실제 저장 경로로 변경 필요
	                drive.setUploader(Employee.builder().employeeNo(mailDto.getEmployee_no()).build());
	                drive.setParam1(mailSaver.getMailNo()); // 메일 참조 연결
	                drive.setSeparatorCode("FL002");
	                drive.setVisibleYn("Y");
	                // DB에 저장
	                driveRepository.save(drive);
	                
	            }
	        }
			if (mailDraftYn.equals("Y")) {
	            MailDraftDto mailDraftDto = new MailDraftDto();
	            mailDraftDto.setMail_no(mailSaver.getMailNo()); // 메일 번호
	            //mailDraftDto.setEmployee_no(receiverNo); // 수신자
	            mailDraftDto.setMail_draft_time(LocalDateTime.now()); // 현재 시간
	
	            MailDraft mailDraft = mailDraftDto.toEntity();
	            mailDraftRepository.save(mailDraft); // 새로 저장
            }
			
			result =1;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
