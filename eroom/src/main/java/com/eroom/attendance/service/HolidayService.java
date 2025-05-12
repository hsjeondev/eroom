//package com.eroom.attendance.service;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import com.eroom.attendance.entity.Holiday;
//import com.eroom.attendance.repository.HolidayRepository;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class HolidayService {
//	@Value("${holiday.api.service-key}")
//	private String serviceKey;
//
//    private final HolidayRepository holidayRepository;
//    private final RestTemplate restTemplate;
//
//    // 공공데이터포털 디코딩된 서비스 키
//    // private final String SERVICE_KEY = "HAYtiTfAeWeyvz93mjGByayQCmSqDk2i9y7rbXXFu4wciEKQ7XY/Ni/HGYI/hVwcc3eSdt7J4qsfoC7I/xKubA==";
//
//    /**
//     * 연도 단위로 1~12월 공휴일 정보를 JSON으로 불러와 DB에 저장
//     */
//    public void saveHolidaysByJson(int year) {
//        for (int month = 1; month <= 12; month++) {
//        	String url = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"
//        	        + "?serviceKey=" + URLEncoder.encode(serviceKey, StandardCharsets.UTF_8) // ← encode 제거
//        	        + "&solYear=" + year
//        	        + "&solMonth=" + String.format("%02d", month)
//        	        + "&_type=json";
//            try {
//                // 1. API 호출
//                String json = restTemplate.getForObject(url, String.class);
//                System.out.println("[✅ API 응답 내용] " + json);  // ← 반드시 추가!
//                // 2. JSON 파싱
//                ObjectMapper objectMapper = new ObjectMapper();
//                JsonNode items = objectMapper.readTree(json)
//                        .path("response").path("body").path("items").path("item");
//
//                // 3. item이 배열일 경우 반복
//                if (items.isArray()) {
//                    for (JsonNode node : items) {
//                        String locdate = node.get("locdate").asText(); // ex: 20250505
//                        String name = node.get("dateName").asText();   // ex: 어린이날
//
//                        // 20250505 → LocalDateTime 변환
//                        LocalDateTime date = LocalDate.parse(locdate, DateTimeFormatter.ofPattern("yyyyMMdd"))
//                                .atStartOfDay();
//
//                        // 4. 중복 저장 방지 후 DB 저장
//                        if (!holidayRepository.existsByHolidayDate(date)) {
//                            Holiday holiday = Holiday.builder()
//                                    .holidayDate(date)
//                                    .holidayName(name)
//                                    .holidayYear(year)
//                                    .holidayRegDate(LocalDateTime.now())
//                                    .build();
//
//                            holidayRepository.save(holiday);
//                        }
//                    }
//                }
//
//            } catch (Exception e) {
//                System.out.println("공휴일 JSON 파싱 실패: " + e.getMessage());
//            }
//        }
//    }
//    
//    // 공휴일 저장 스케줄러
//    @Scheduled(cron = "0 * * * * *") // 매 분마다 실행
//    public void scheduleHolidaySave() {
//    	int year = LocalDate.now().getYear(); // 현재 연도 기준
//    	System.out.println("[공휴일 스케줄러] "+year+"년 공휴일 저장 시작");
//    	saveHolidaysByJson(year);
//    	System.out.println("[공휴일 스케줄러] 저장 완료");
//    }
//    
//    
//    
//}
