# 📘 Eroom 그룹웨어  
**IT 기업에 최적화된 통합 업무 시스템**으로, 개발자 중심의 협업 환경을 고려해 설계한 그룹웨어입니다.  
주소록, 이메일, 채팅, 결재, 프로젝트 관리, 캘린더, 예약, 게시판, 드라이브, 마이페이지, 알림, 설문조사, 챗봇 등  
총 **14개의 통합 메뉴 + 로그인 시스템 + 관리자 기능**을 제공합니다.

---

## 🔗 프로젝트 링크  
- GitHub: [https://github.com/yourteam/eroom](https://github.com/yourteam/eroom)
- 요구사항 정의서 : [Eroom_요구사항_정의서](https://docs.google.com/spreadsheets/d/1w7BuOShIZBw-MbzxRealUGnAmTkDN9zuUwkbWGjalkE/edit?gid=0#gid=0)
- 화면 설계서 : [Eroom_화면_설계서](https://www.figma.com/design/o4UqCm1Q2Fwuk7Gh2jfFCK/Eroom-IT-%ED%9A%8C%EC%82%AC-%EA%B7%B8%EB%A3%B9%EC%9B%A8%EC%96%B4--%ED%99%94%EB%A9%B4-%EC%A0%95%EC%9D%98%EC%84%9C?node-id=1248-35561&t=14geTLj39bS1ykkJ-1)
- 테이블 정의서 : [Eroom_테이블_정의서](https://docs.google.com/spreadsheets/d/1q5LjIvu9inH75tKSUmEjuYY-0p3ne0m57-tAbesTU2s/edit?gid=0#gid=0)

---

## 🕸️ ERD
![Copy of Copy of TeamEroom ERD_20250514_ver05](https://github.com/user-attachments/assets/e81831ff-17b8-403b-bcb0-40c3a476f384)


---

## 🧩 개요  
- `Eroom`은 **IT 기업 조직의 실무 흐름**을 기준으로 기획된 통합 그룹웨어입니다.  
- 단순한 일정 관리나 게시판 기능을 넘어서, **PR 및 코드 품질 분석**, **회의록 자동 요약**, **근태 관리 및 연차 처리**, **WebSocket 기반 실시간 채팅/알림**, **PDF 문서화 결재 시스템**, **자동 로그인 및 관리자 기능** 등 개발 조직 특화 기능이 포함되어 있습니다.  
- **리눅스 서버 기반 Docker 배포**, **Jenkins CI/CD**, **cloudflared를 통한 HTTPS 도메인 연결**까지 포함된 실제 서비스 수준의 배포 경험을 반영하였습니다.

---

## 🛠 기술 스택

| 구분 | 기술 | 설명 |
|------|------|------|
| **백엔드** | Java, Spring Boot, Spring MVC, Spring Security, MyBatis, JPA | REST API 설계, 로그인 인증, DB 연동 |
| **프론트엔드** | Thymeleaf, JavaScript, Bootstrap | 반응형 UI, 서버 템플릿 렌더링 |
| **실시간 통신** | WebSocket (Spring WebSocket) | 실시간 채팅 및 알림 기능 구현 |
| **데이터베이스** | MariaDB, MySQL Workbench, HeidiSQL | 데이터 모델링 및 조회 최적화 |
| **배포/운영** | Linux, Docker, Jenkins, cloudflared | CI/CD 자동화, 도메인 설정, 리버스 프록시 구성 |
| **기타** | GitHub API, SonarCloud API, TMAP API, ChatGPT API | 외부 서비스 연동 및 자동화 처리 |

---

## 🚀 주요 기능

| 메뉴 / 영역 | 기능 설명 |
|--------------|------------|
| 🔐 **로그인** | Spring Security 기반 로그인, 자동 로그인(remember-me), 권한 기반 접근 제어 |
| 🧑‍💼 **관리자 페이지** | 사원 정보 관리, 차량 등록/수정/삭제, 회의실 등록/수정/삭제 등 관리자 기능 제공 |
| 🏠 **홈** | 뉴스 및 날씨 API 연동 |
| 📇 **주소록** | 회사 및 협력업체 구분, 개인 메모 기능 포함 |
| 📧 **이메일** | 중요 메일 분류, 휴지통, 다중 수신자 전송 |
| 💬 **채팅** | WebSocket 기반 실시간 1:1 및 그룹 채팅, 파일 전송, 읽음 확인, 실시간 알림 |
| 📑 **결재** | 결재자/합의자/참조자 지정, 결재 상태 관리, 승인 시 알림 발송, PDF 문서 자동 생성 |
| 📁 **프로젝트** | 칸반보드, GitHub PR 목록 조회, SonarCloud 코드 품질 시각화, 회의록 ChatGPT 자동 요약, 완료일 기준 스케줄러 반영 |
| 📅 **캘린더** | 개인/팀/부서/회사 일정 통합, 연차 결재 승인 시 회사 일정에 자동 반영 |
| 🗓 **예약** | 차량 및 회의실 예약, 차량 예약 시 TMAP API로 위치 정보 검색 기능 포함 |
| 🗂 **게시판** | 공지사항 등록, 긴급공지 설정 기능 포함 |
| 📂 **드라이브** | 개인, 팀, 부서, 회사 드라이브, 멀티 파일 업로드, 다운로드, 확장자별 용량 확인 |
| 👤 **마이페이지** | 출퇴근 및 근태 기록 Excel 다운로드, 연차 자동 계산 및 부여 기능 |
| 🔔 **알림** | WebSocket 기반 실시간 알림: 캘린더, 채팅, 결재, 메일 등 기능 연동 알림 제공 |
| 📋 **설문조사** | 익명 투표, 복수 선택 투표 기능 |
| 💬 **Eroom 챗봇** | 프로젝트 기능 관련 질문에 대해 ChatGPT 기반 자동 응답 제공 (시스템 메시지 기반 커스터마이징)

---

## 📧 문의  
Email: hsjeondev@gmail.com
