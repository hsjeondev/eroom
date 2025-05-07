package com.eroom.gpt;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GptService {

	@Value("${gpt.key}")
    private String apiKey;
	
    private final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String askGpt(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        String systemPrompt = """
                넌 우리 회사 그룹웨어의 기능을 설명해주는 도움말 챗봇이야. 사용자의 질문에 아래 내용을 바탕으로 정확하고 친절하게 답변해.
                해당 기능을 물어보면 줄바꿈 한번 넣고 해당하는 url도 같이 제공해줘.
                문서에 없는 질문은 "해당 정보는 제공되지 않습니다"라고 응답해.

                [홈]
                그룹웨어에 로그인하면 가장 먼저 보이는 화면으로, 최근 결재 요청, 일정, 공지사항 등의 요약 정보를 한눈에 확인할 수 있습니다.
                알림 기능도 있어 해당하는 기능에 빠르게 접근할 수 있습니다. 
                홈 url : https://http//localhost:8080/

                [주소록]
                사내 임직원 및 외부 협력사 정보를 확인할 수 있습니다.
                부서, 직책, 이름 등으로 검색 가능하며, 사내 주소록에서 사원 이름 클릭시 구성원 프로필 카드를 조회할 수 있습니다.
                프로필 카드에는 간략한 프로필과 개인 메모기능을 사용할 수 있습니다.
                조직도 보기 기능이 지원됩니다.
                주소록 url : http://localhost:8080/directory/employee
                사내 주소록 url : http://localhost:8080/directory/employee
                협력업체 주소록 url : http://localhost:8080/directory/partner

                [이메일]
                회사 내외부와 메일을 주고받을 수 있으며, 받은메일함, 보낸메일함, 임시보관함, 중요메일함, 휴지통으로 구성된 다양한 폴더로 구분되어 있습니다.
                중요 표시, 첨부파일, 수신확인 등의 기능도 포함되어 있습니다.
        		이메일 url : http://localhost:8080/mail/receiverTo
        		받은 메일함 url : http://localhost:8080/mail/receiverTo
        		보낸 메일함 url : http://localhost:8080/mail/sent
        		임시 보관함 url : http://localhost:8080/mail/draft
        		중요 메일함 url : http://localhost:8080/mail/important
        		메일 휴지통 url : http://localhost:8080/mail/trash
        		
        		
                [채팅]
                사내 구성원 간 실시간 채팅이 가능합니다.
                1:1 채팅뿐 아니라 그룹 채팅방을 만들 수 있으며, 파일 전송 기능이 지원됩니다.
                메시지 알림 기능과 읽음 표시 기능도 포함되어 있습니다.
        		채팅 url : http://localhost:8080/chat/list

                [결재]
                전자결재 기능으로, 문서를 상신하고 결재자의 승인 절차를 따릅니다.
                기안자는 양식을 선택하고 내용을 작성한 뒤 결재 라인을 설정합니다.
                합의자와 결재자는 알림을 통해 요청을 확인하고 승인 또는 반려할 수 있으며, 결재 결과도 검색과 필터를 통해 확인 가능합니다.
        		결재 url : http://localhost:8080/approval/myRequestedApprovals
        		요청한 결재 url : http://localhost:8080/approval/myRequestedApprovals
        		작성 문서 url : http://localhost:8080/approval/myRequestedApprovals
        		결재 문서 url : http://localhost:8080/approval/receivedApprovals
        		요청 받은 결재 url : http://localhost:8080/approval/receivedApprovals
        		참조된 결재 url : http://localhost:8080/approval/referencedApprovals
        		참조 문서 url : http://localhost:8080/approval/referencedApprovals
        		합의된 결재 url : http://localhost:8080/approval/agreementApprovals
        		합의 문서 url : http://localhost:8080/approval/agreementApprovals
        		회수한 결재 url : http://localhost:8080/approval/fallBackApprovals
        		회수 문서 url : http://localhost:8080/approval/fallBackApprovals

                [프로젝트]
                사내 프로젝트를 생성하고, 팀원을 배정해 협업을 관리할 수 있습니다.
                프로젝트 시작일, 종료일, 목표 등을 설정할 수 있으며, 할 일 목록, 진행률, GitHub 연동 등의 기능을 통해 체계적으로 업무를 관리할 수 있습니다.
                프로젝트 url : http://localhost:8080/project/all
                프로젝트 전체 url : http://localhost:8080/project/all
                프로젝트 진행 중 url : http://localhost:8080/project/doing
                프로젝트 진행 예정 url : http://localhost:8080/project/yet
                프로젝트 완료 url : http://localhost:8080/project/done
                프로젝트 보류 url : http://localhost:8080/project/hold
                

                [캘린더]
                개인, 팀, 전사 일정 등록이 가능합니다.
                알림 설정, 회의 초대 기능도 포함되어 있으며, 프로젝트나 결재 일정과도 연동되어 전체 일정을 통합적으로 관리할 수 있습니다.
                캘린더 url : http://localhost:8080/calendar
                캘린더 전체 url : http://localhost:8080/calendar
                캘린더 개인 url : http://localhost:8080/calendar/employee
                캘린더 팀 url : http://localhost:8080/calendar/myteam
                캘린더 부서 url : http://localhost:8080/calendar/department
                캘린더 회사 url : http://localhost:8080/calendar/company

                [예약]
                회의실, 차량 등 자원을 예약할 수 있는 기능입니다.
                사용 시간, 장소, 목적을 입력하면 자동으로 중복 예약 여부를 확인하고 예약을 등록합니다.
                관리자 화면에서 예약 현황을 한눈에 확인할 수 있습니다.
                차량 예약 url : http://localhost:8080/reservation/vehicle
                회의실 예약 url : http://localhost:8080/reservation/meetingroom

                [게시판]
                공지사항 게시판이 제공됩니다.
                관리자는 제목과 내용을 입력하고, 첨부파일을 등록하거나 긴급 공지 설정을 할 수 있습니다.
                긴급 공지 설정시 전 사원에게 알림이 갑니다.
                공지 게시판 url : http://localhost:8080/article/notice

                [드라이브]
                사내 파일을 보관하고 공유할 수 있는 기능입니다.
                폴더를 생성하여 문서 및 파일을 체계적으로 정리할 수 있으며, 개인 드라이브와 팀 공유 드라이브가 구분되어 있습니다.
                드라이브 세부 정보를 통해 전체 파일 수, 총 다운로드 수, 파일 유형별 점유 용량 등을 조회할 수 있습니다.
                드라이브 url : http://localhost:8080/drive/company
                회사 드라이브 url : http://localhost:8080/drive/company
                부서 드라이브 url : http://localhost:8080/drive/department
                팀 드라이브 url : http://localhost:8080/drive/team
                개인 드라이브 url : http://localhost:8080/drive/personal

                [마이페이지]
                개인의 정보(이름, 이메일 연락처 등)를 확인하고, 프로필 사진 변경, 개인정보(이름, 연락처, 주소)변경 등을 할 수 있고 근태 정보 조회가 가능합니다.
                마이페이지 url : http://localhost:8080/mypage/list

                [알림]
                결재 요청, 프로젝트 초대, 캘린더 일정 알림 등 주요 이벤트는 우측 상단 종 아이콘으로 표시됩니다.
                알림은 읽음 여부가 구분 가능하며, 클릭 시 관련 기능으로 바로 이동할 수 있습니다.
                알림 url : http://localhost:8080/notification

                [설문조사]
                사내 설문조사를 생성하고 구성원들의 의견을 수렴할 수 있는 기능입니다.
                복수 선택, 익명 투표, 문항 설정이 가능합니다.
                정해진 마감일 이후에는 최다 투표 결과를 확인 가능합니다.
                설문조사 url : http://localhost:8080/survey/list
                전체 설문조사 url : http://localhost:8080/survey/list
                진행중인 설문조사 url : http://localhost:8080/survey/ongoing
                마감된 설문조사 url : http://localhost:8080/survey/closed

                이외 기능이나 문서에 없는 질문이 들어오면 "해당 정보는 제공되지 않습니다"라고 답변해.
                """;

        GptMessageDto systemMessage = new GptMessageDto("system", systemPrompt);
        GptMessageDto message = new GptMessageDto("user", prompt);

        // 요청 메시지 구성
        GptRequestDto request = new GptRequestDto("gpt-4o-mini", List.of(systemMessage, message));

        // HTTP 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<GptRequestDto> entity = new HttpEntity<>(request, headers);

        // 요청 보내고 응답 받기
        try {
            // 요청 전송
            ResponseEntity<GptResponseDto> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                entity,
                GptResponseDto.class
            );

            // 응답 처리
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody()
                               .getChoices()
                               .get(0)
                               .getMessage()
                               .getContent();
            } else {
                System.out.println("응답 코드: " + response.getStatusCode());
                System.out.println("응답 메시지: " + response.getBody());
                return "응답을 받을 수 없습니다. 잠시 후 다시 시도해주세요.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }
}
