package com.eroom.notification.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.approval.entity.ApprovalAlarm;
import com.eroom.approval.service.ApprovalAlarmService;
import com.eroom.calendar.entity.CalendarAlarm;
import com.eroom.calendar.service.CalendarAlarmService;
import com.eroom.chat.entity.ChatAlarm;
import com.eroom.chat.entity.ChatroomAttendee;
import com.eroom.chat.service.ChatMessageService;
import com.eroom.mail.entity.MailAlarm;
import com.eroom.mail.service.MailService;
import com.eroom.notification.dto.AlarmDto;
import com.eroom.notification.entity.Alarm;
import com.eroom.notification.repository.AlarmRepository;
import com.eroom.security.EmployeeDetails;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {
	
	private final AlarmRepository alarmRepository;
	private final ApprovalAlarmService approvalAlarmService;
	private final CalendarAlarmService calendarAlarmService;
	private final ChatMessageService chatMessageService;
	private final MailService mailService;
	
	 //알림 페이지 목록 조회
	 @Transactional
	 public List<AlarmDto> getMyAlarms() {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        EmployeeDetails user = (EmployeeDetails) auth.getPrincipal();
	        Long employeeNo = user.getEmployeeNo();

	        List<Alarm> alarms = alarmRepository.findAllWithDetailsByEmployeeNo(employeeNo);
	        List<AlarmDto> result = new ArrayList<>();

	        for (Alarm alarm : alarms) {
	            AlarmDto dto = new AlarmDto().toDto(alarm);

//	            if ("R001".equals(alarm.getSeparatorCode()) && alarm.getCalendarAlarm() != null) {
//	                dto.setCalendarAlarm(new CalendarAlarmDto().toDto(alarm.getCalendarAlarm()));
//	            } else if ("R002".equals(alarm.getSeparatorCode()) && alarm.getChatAlarm() != null) {
//	                dto.setChatAlarm(new ChatAlarmDto().toDto(alarm.getChatAlarm()));
//	            } else if ("R003".equals(alarm.getSeparatorCode()) && alarm.getMailAlarm() != null) {
//	                dto.setMailAlarm(new MailAlarmDto().toDto(alarm.getMailAlarm()));
//	            } else if ("R004".equals(alarm.getSeparatorCode()) && alarm.getApprovalAlarm() != null) {
//	                dto.setApprovalAlarm(new ApprovalAlarmDto().toDto(alarm.getApprovalAlarm()));
//	            }

	            result.add(dto);
	        }

	        return result;
	    }
	 
	 //헤더 종 누르면 목록조회
	 public List<AlarmDto> getUnreadAlarms(Long loginEmployeeNo) {
		    List<Alarm> alarms = alarmRepository.findByEmployeeNoAndReadYnOrderByRegDateDesc(loginEmployeeNo, "N");

		    List<AlarmDto> result = new ArrayList<>();
		    for (Alarm alarm : alarms) {
		        AlarmDto dto = new AlarmDto().toDto(alarm);
		        result.add(dto);
		    }

		    return result;
		}
	 
	 //전체 읽음 처리 버튼
	 @Transactional
	 public void markAllAsRead(Long employeeNo) {
	     alarmRepository.updateAllToReadByEmployeeNo(employeeNo);
	 }
	 
	 //목록에서 일정 클릭하면 N을 Y로 변경
	 public Map<String, Object> markAsRead(Long alarmId) {
		 Map<String, Object> map = new HashMap<String, Object>();
	     Alarm target = alarmRepository.findById(alarmId).orElse(null);

	     if (target != null && "N".equals(target.getReadYn())) {
	         Alarm updated = Alarm.builder()
	             .alarmNo(target.getAlarmNo())
	             .param1(target.getParam1())
	             .separatorCode(target.getSeparatorCode())
	             .employeeNo(target.getEmployeeNo())
	             .readYn("Y") // 읽음 처리
	             .regDate(target.getRegDate())
	             .build();
	         

	         alarmRepository.save(updated);
	         // 확인용 이전 Alarm Entity 반환
	         if("R001".equals(target.getSeparatorCode())) {
	        	 // pk 넣든 필요한 거 넣으세요
	        	 CalendarAlarm calendarAlarm = calendarAlarmService.findAlarmOne(target.getParam1());
	        	 map.put("separator", calendarAlarm.getSeparator());
	        	 map.put("separator_code", "R001");
	        	 return map;
	         } else if ("R002".equals(target.getSeparatorCode())) {
        	    ChatAlarm chatAlarm = chatMessageService.findOne(target.getParam1());

        	    if (chatAlarm != null &&
        	        chatAlarm.getChatMessage() != null &&
        	        chatAlarm.getChatMessage().getChatroom() != null) {

        	        Long chatRoomNo = chatAlarm.getChatMessage().getChatroom().getChatroomNo();
        	        System.out.println("chatAlarm: " + chatAlarm);
        	        System.out.println("chatMessageNo: " + chatAlarm.getChatMessage().getChatMessageNo());
        	        System.out.println("chatroomNo: " + chatRoomNo);

        	        map.put("pk", target.getParam1()); // chat_alarm_no
        	        map.put("separator_code", "R002");
        	        map.put("roomNo", chatRoomNo); // ✅ 올바르게 할당

        	        Long senderNo = chatAlarm.getChatMessage().getSenderMember().getEmployeeNo();
        	        Long targetEmpNo = null;

        	        var chatroom = chatAlarm.getChatMessage().getChatroom();
        	        if (chatroom.getChatroomMapping() != null) {
        	            for (ChatroomAttendee attendee : chatroom.getChatroomMapping()) {
        	                if (attendee.getAttendee() != null &&
        	                    !attendee.getAttendee().getEmployeeNo().equals(senderNo)) {
        	                    targetEmpNo = attendee.getAttendee().getEmployeeNo();
        	                    break;
        	                }
        	            }
        	        }
        	        if (targetEmpNo != null) {
        	            map.put("targetEmpNo", targetEmpNo);
        	        }
        	    }
        	    return map;
	        	    
        	 } else if("R003".equals(target.getSeparatorCode())) {
        		 MailAlarm mailAlarm= mailService.findAlarmOne(target.getParam1());
        		 if (mailAlarm != null && mailAlarm.getMailAlarmNo() != null) {
        		        map.put("pk", mailAlarm.getMailReceiver().getMail().getMailNo()); // 실제 메일 번호
        		        map.put("separator_code", "R003");
        		        System.out.println(map);
        		        return map;
        		    }
	        	 
	         } else if("R004".equals(target.getSeparatorCode())) {
	        	 ApprovalAlarm approvalAlarm = approvalAlarmService.findAlarmOne(target.getParam1());
	        	 map.put("pk", approvalAlarm.getApproval().getApprovalNo());
	        	 map.put("separator_code", "R004");
	        	 return map;
	         }
	         return null;
	     } else {
	    	 return null;
	     }
	 }
	 
	 @Transactional
	 public Map<String, List<AlarmDto>> getGroupedAlarms() {
	     //  로그인한 사용자 정보에서 employeeNo 추출
	     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	     EmployeeDetails user = (EmployeeDetails) auth.getPrincipal();
	     Long employeeNo = user.getEmployeeNo();

	     //  해당 직원의 모든 알림 가져오기
	     List<Alarm> alarms = alarmRepository.findAllWithDetailsByEmployeeNo(employeeNo);

	     // 날짜별 그룹핑 맵
	     Map<String, List<AlarmDto>> groupedMap = new LinkedHashMap<>();

	     for (Alarm alarm : alarms) {
	         AlarmDto dto = new AlarmDto().toDto(alarm);

	         // 상세 알림 설정 
//	         if ("R001".equals(alarm.getSeparatorCode()) && alarm.getCalendarAlarm() != null) {
//	             dto.setCalendarAlarm(new CalendarAlarmDto().toDto(alarm.getCalendarAlarm()));
//	         } else if ("R002".equals(alarm.getSeparatorCode()) && alarm.getChatAlarm() != null) {
//	             dto.setChatAlarm(new ChatAlarmDto().toDto(alarm.getChatAlarm()));
//	         } else if ("R003".equals(alarm.getSeparatorCode()) && alarm.getMailAlarm() != null) {
//	             dto.setMailAlarm(new MailAlarmDto().toDto(alarm.getMailAlarm()));
//	         } else if ("R004".equals(alarm.getSeparatorCode()) && alarm.getApprovalAlarm() != null) {
//	             dto.setApprovalAlarm(new ApprovalAlarmDto().toDto(alarm.getApprovalAlarm()));
//	         }

	         //날짜 키 (yyyy-MM-dd)
	         String dateKey = dto.getReg_date().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

	         // 그룹에 추가
	         groupedMap.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(dto);
	     }

	     return groupedMap;
	 }
	 
}