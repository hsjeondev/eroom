package com.eroom.attendance.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.attendance.dto.AttendanceDto;
import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.attendance.entity.Attendance;
import com.eroom.attendance.repository.AnnualLeaveRepository;
import com.eroom.attendance.repository.AttendanceRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
@Setter
public class AttendanceService {
	
	private final AttendanceRepository attendanceRepository;
	private final AnnualLeaveRepository annualLeaveRepository;
	
	// 출퇴근 기록
	public Attendance recordAttendance(AttendanceDto dto){
		
		LocalDateTime now = LocalDateTime.now();
		
		// 로그인 인증 정보
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		Long employeeNo = employeeDetail.getEmployee().getEmployeeNo();
		
		// 지각 조퇴 여부 default 값
		String lateYn = "N";
		String earlyLeaveYn = "N";
		
		// 오늘 출근한 기록
		// toLocalDate() -> 날짜만 꺼냄, atStartOfDay() -> 그 날짜의 자정(00:00)
		LocalDateTime todayStart = now.toLocalDate().atStartOfDay(); 
		// 오늘 + 1일 자정(다음날 00시) 
		LocalDateTime todayEnd = todayStart.plusDays(1);
		// 오늘 출근 기록 조회
		Attendance todayAttendance = attendanceRepository.findLastCheckInToday(employeeNo, todayStart, todayEnd);
		// 타입이 출근일때
		if("checkIn".equals(dto.getAttendanceType())) {
			// 이미 출근 한 경우
			if(todayAttendance != null) return null;
			
			// 지각 체크
			if(now.getHour() > 9 || (now.getHour() == 9 && now.getMinute() > 0)) lateYn = "Y";
			// 출근 기록 저장
			Attendance attendance = Attendance.builder()
						.employee(Employee.builder().employeeNo(employeeNo).build())
						.attendanceCheckInTime(now)
						.attendanceLateYn(lateYn)
						.build();
			return	attendanceRepository.save(attendance);
			
		}else if("checkOut".equals(dto.getAttendanceType())) { // 퇴근일때

			// 출근 기록 없으면 퇴근 X
			if(todayAttendance == null) return null;
			
			// 퇴근 한 경우
			if(todayAttendance.getAttendanceCheckOutTime() != null) return null;
			// 18 시 이전이면 조퇴
			if(now.getHour() < 18) earlyLeaveYn = "Y";
			
			// 기존 출근 기록에 퇴근 정보 추가
			todayAttendance.setAttendanceCheckOutTime(now);
			todayAttendance.setAttendanceEarlyLeaveYn(earlyLeaveYn);
			
			return attendanceRepository.save(todayAttendance);
					

		}
		
		return null;
	}
	
	// 홈에서 출퇴근 상태, 시간 반환
	public Map<String,String> getTodayAttendanceStatusAndTime(Long employeeNo){
		Map<String,String> resultMap = new HashMap<>();
		
		/*
		 * // 현재 로그인한 정보 Authentication authentication =
		 * SecurityContextHolder.getContext().getAuthentication(); EmployeeDetails
		 * employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		 */
		// 오늘 날짜
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
		LocalDateTime todayEnd = todayStart.plusDays(1);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		
		// 출근 기록 전체 최신순 조회
		List<Attendance> checkIns = attendanceRepository.findTodayAttendanceList(employeeNo, todayStart, todayEnd);
		
		if(checkIns.isEmpty()) {
			resultMap.put("attendanceStatus", "notCheckedIn");
			resultMap.put("attendanceTime", "");
		}else {
			Attendance recent = checkIns.get(0);
			String attendanceStatus = "";
			String attendanceTime = "";

			if (recent.getAttendanceCheckOutTime() == null) {
				attendanceStatus = "checkedIn";
				attendanceTime = recent.getAttendanceCheckInTime().format(formatter);
			} else {
				attendanceStatus = "checkedOut";
				attendanceTime = recent.getAttendanceCheckOutTime().format(formatter);
			}

			resultMap.put("attendanceStatus", attendanceStatus);
			resultMap.put("attendanceTime", attendanceTime);
		}
		return resultMap;
	}
	
	
	// 근태 기록 전체 조회
	public List<Attendance> selectAttendanceList(){
		
		// 현재 로그인한 정보
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		EmployeeDetails employeeDetail = (EmployeeDetails)authentication.getPrincipal();
		
		Long employeeNo = employeeDetail.getEmployee().getEmployeeNo(); 
		
		return attendanceRepository.findByEmployee_EmployeeNoOrderByAttendanceCheckInTimeDesc(employeeNo);
		
		
	}
	// 연차 정보 조회
	public AnnualLeave selectAnnualLeaveByEmployeeNo(Long employeeNo) {
		
		return annualLeaveRepository.findByEmployee_EmployeeNo(employeeNo);
	}
	
	// 근무 기록이 있는 월 조회
	public List<String> selectAttendanceMonthList(Long employeeNo){
		return attendanceRepository.findDistinctAttendanceMonth(employeeNo);
	}
	
	// 해당 월의 근태 기록 조회
	public List<AttendanceDto> selectAttendanceListByMonth(Long employeeNo, String month){
		List<Attendance> attendanceList;
		
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM");
		// 현재 날짜 기준으로 현재 월 가져오기
		String currentMonth = (month != null && !month.isEmpty()) ? month : LocalDateTime.now().format(formatter);
		// YearMonth -> 연도와 월만 저장
		YearMonth yearMonth = YearMonth.parse(currentMonth,formatter);
		
		// 해당 월의 첫날, 마지막날 계산
		LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
		LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
		
		// 월별 출근 기록 조회
		attendanceList = attendanceRepository.findByEmployee_EmployeeNoAndAttendanceCheckInTimeBetweenOrderByAttendanceCheckInTimeDesc(employeeNo, start, end);
		
		// 조회된 결과를 DTO로 변환
		List<AttendanceDto> attendanceDtoList = new ArrayList<>();
		for(Attendance attendance : attendanceList) {
			attendanceDtoList.add(new AttendanceDto().toDto(attendance));
		}
		
		return attendanceDtoList;
	}
	
	// 로그인한 사용자 기준 근태 통계 데이터
	public Map<String,Object> getAttendanceChartData(EmployeeDetails employeeDetail){
		Long employeeNo = employeeDetail.getEmployee().getEmployeeNo();
		
		// 평균 출근 시간 계산 리스트
		List<LocalTime> checkInTimes = new ArrayList<>();
		
		// 이번 달 시작일, 마지막일
		YearMonth currentMonth = YearMonth.now();
		LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
		LocalDateTime end = currentMonth.atEndOfMonth().atTime(23, 59, 59);
		
		// 사원의 이번 달 근태 기록 전체 조회
		List<Attendance> records = attendanceRepository.findByEmployeeAndDateRange(employeeNo,start,end);
		
		// 근태 요약 카운트
		int checkinDays = 0; // 출근일 수
		int lateCount = 0; // 지각 횟수
		int earlyLeaveCount = 0; // 조퇴 횟수
		
		int totalMinutes = 0; // 총 근무시간(분 단위)
		
		// 일별 근무시간 저장 Map -> 근무시간은 소수점까지 필요함.
		Map<String,Double> workTimePerDay = new LinkedHashMap<>();
		
		// 요일별 근무시간 평균 계산 
		// 각 요일 해당하는 근무시간들 List로 저장한 뒤 평균 계산
		Map<String,List<Double>> workTimePerWeekday = new HashMap<>();
		
		for(Attendance a : records) {
			// 출근 or 퇴근 시간이 비어있으면 계산 제외
			if(a.getAttendanceCheckInTime() == null || a.getAttendanceCheckOutTime() == null) continue;
			if(a.getAttendanceCheckInTime() != null && a.getAttendanceCheckOutTime() != null) {
				totalMinutes += Duration.between(a.getAttendanceCheckInTime(), a.getAttendanceCheckOutTime()).toMinutes(); // 총 근무시간
			}
			checkinDays++; // 출근일 수 증가
			if("Y".equals(a.getAttendanceLateYn())) lateCount++; // 지각 횟수 증가
			if("Y".equals(a.getAttendanceEarlyLeaveYn())) earlyLeaveCount++; // 조퇴 횟수 증가
			
			// 일별 근무시간 계산
			LocalDateTime checkIn = a.getAttendanceCheckInTime();
			LocalDateTime checkOut = a.getAttendanceCheckOutTime();
			double workTime = Duration.between(checkIn, checkOut).toMinutes() / 60.0; // 초단위로 계산 후 시 단위로 변환
//					(checkOut.getHour() - checkIn.getHour()) + ((checkOut.getMinute() - checkIn.getMinute()) / 60.0);
			
			workTimePerDay.put(checkIn.toLocalDate().toString(), workTime); // 날짜별 근무시간 저장

			
			// 요일별 근무시간 분류
			// getDayOfWeek() -> 요일을 반환 -> toString() -> 요일을 문자열로 변환
			String weekday = checkIn.getDayOfWeek().toString(); // 요일
			// 해당 요일 키가 Map에 없으면 새로 생성
			workTimePerWeekday.putIfAbsent(weekday, new ArrayList<>());
			// 해당 요일 리스트에 근무시간 추가
			workTimePerWeekday.get(weekday).add(workTime);
			
			checkInTimes.add(checkIn.toLocalTime()); // 출근 시간 리스트에 추가
			
		}
		
		long hours = (long)(totalMinutes / 60); // 총 근무시간(시)
		long minutes = (long)(totalMinutes % 60); // 총 근무시간(분)
		String totalWorkTime = hours + "시간 " + minutes + "분"; // 총 근무시간 문자열
		
		// 요일별 평균 근무시간 계산
		Map<String,Double> weekdayAvg = new LinkedHashMap<>();
		// workTimePerWeekday.keySet() -> 요일별로 저장된 Map의 키값(요일) 반환
		for (String day : workTimePerWeekday.keySet()) {
			// 해당 요일의 근무시간 리스트 가져오기
			List<Double> workTimes = workTimePerWeekday.get(day);
			// 근무시간이 없으면 평균 0 , 있으면 평균 계산
			double sum = 0;
			for (Double time : workTimes) {
				sum += time;
			}
			// 평균 계산 : 총합 / 데이터 개수
			double avg = sum / workTimes.size();
			// 결과에 요일별 평균 저장
			weekdayAvg.put(day, avg);
		}
		
		// 평균 출근 시간 계산
		String averageCheckInTime = "";
		if(!checkInTimes.isEmpty()) {
			int totalCheckInMinutes = 0;
			for (LocalTime time : checkInTimes) {
				totalCheckInMinutes += time.getHour() * 60 + time.getMinute(); // 시 * 60 + 분
			}
			int avgMinutes = totalCheckInMinutes / checkInTimes.size(); // 평균 시
			int avgHours = avgMinutes / 60; // 평균 시
			int avgMinutesRemain = avgMinutes % 60; // 평균 분
			averageCheckInTime = String.format("%02d:%02d", avgHours, avgMinutesRemain); // 시:분 형식으로 변환
		}
		
		// 출근률 계산
		int weekdayCount = 0; // 주말 제외 출근일 수
		LocalDate startDate = currentMonth.atDay(1); // 이번 달 첫날
		LocalDate endDate = currentMonth.atEndOfMonth(); // 이번 달 마지막 날
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) { // 이번 달 날짜 반복
			// 토요일, 일요일 제외
			if (date.getDayOfWeek().getValue() < 6) { // 월~금
				weekdayCount++;
			}	
		}
		
		String attendanceRate = "";
		if(weekdayCount > 0 ) {
			double rate = (checkinDays * 100.0) / weekdayCount; // 출근일 수 / 주말 제외 출근일 수
			attendanceRate = String.format("%.0f", rate) + "%"; // 소수점 0자리까지
			
		}
		
		// 결과 Map 생성
		Map<String,Object> result = new HashMap<>();
		result.put("checkinDays", checkinDays); // 출근일 수
		result.put("lateCount", lateCount); // 지각 횟수
		result.put("earlyLeaveCount", earlyLeaveCount); // 조퇴 횟수
		result.put("workTimePerDay", workTimePerDay); // 일별 근무시간
		result.put("weekdayAvg", weekdayAvg); // 요일별 평균 근무시간
		result.put("totalWorkTime", totalWorkTime); // 총 근무시간
		result.put("averageCheckInTime", averageCheckInTime); // 평균 출근시간
		result.put("attendanceRate", attendanceRate); // 출근률
		
		return result;
		
	}
	
	// employeeNo로 근태 통계 데이터 조회
	public Map<String, Object> getAttendanceChartDataByEmployeeNo(Long employeeNo){
		
		// 평균 출근 시간 계산 리스트
		List<LocalTime> checkInTimes = new ArrayList<>();
		
		// 이번 달 시작일, 마지막일
		YearMonth currentMonth = YearMonth.now();
		LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
		LocalDateTime end = currentMonth.atEndOfMonth().atTime(23, 59, 59);
		
		// 사원의 이번 달 근태 기록 전체 조회
		List<Attendance> records = attendanceRepository.findByEmployeeAndDateRange(employeeNo,start,end);
		
		// 근태 요약 카운트
		int checkinDays = 0; // 출근일 수
		int lateCount = 0; // 지각 횟수
		int earlyLeaveCount = 0; // 조퇴 횟수
		
		int totalMinutes = 0; // 총 근무시간(분 단위)
		
		// 일별 근무시간 저장 Map -> 근무시간은 소수점까지 필요함.
		Map<String,Double> workTimePerDay = new LinkedHashMap<>();
		
		// 요일별 근무시간 평균 계산 
		// 각 요일 해당하는 근무시간들 List로 저장한 뒤 평균 계산
		Map<String,List<Double>> workTimePerWeekday = new HashMap<>();
		
		for(Attendance a : records) {
			// 출근 or 퇴근 시간이 비어있으면 계산 제외
			if(a.getAttendanceCheckInTime() == null || a.getAttendanceCheckOutTime() == null) continue;
			if(a.getAttendanceCheckInTime() != null && a.getAttendanceCheckOutTime() != null) {
				totalMinutes += Duration.between(a.getAttendanceCheckInTime(), a.getAttendanceCheckOutTime()).toMinutes(); // 총 근무시간
			}
			checkinDays++; // 출근일 수 증가
			if("Y".equals(a.getAttendanceLateYn())) lateCount++; // 지각 횟수 증가
			if("Y".equals(a.getAttendanceEarlyLeaveYn())) earlyLeaveCount++; // 조퇴 횟수 증가
			
			// 일별 근무시간 계산
			LocalDateTime checkIn = a.getAttendanceCheckInTime();
			LocalDateTime checkOut = a.getAttendanceCheckOutTime();
			double workTime = Duration.between(checkIn, checkOut).toMinutes() / 60.0; // 초단위로 계산 후 시 단위로 변환
//					(checkOut.getHour() - checkIn.getHour()) + ((checkOut.getMinute() - checkIn.getMinute()) / 60.0);
			
			workTimePerDay.put(checkIn.toLocalDate().toString(), workTime); // 날짜별 근무시간 저장

			
			// 요일별 근무시간 분류
			// getDayOfWeek() -> 요일을 반환 -> toString() -> 요일을 문자열로 변환
			String weekday = checkIn.getDayOfWeek().toString(); // 요일
			// 해당 요일 키가 Map에 없으면 새로 생성
			workTimePerWeekday.putIfAbsent(weekday, new ArrayList<>());
			// 해당 요일 리스트에 근무시간 추가
			workTimePerWeekday.get(weekday).add(workTime);
			
			checkInTimes.add(checkIn.toLocalTime()); // 출근 시간 리스트에 추가
			
		}
		
		long hours = (long)(totalMinutes / 60); // 총 근무시간(시)
		long minutes = (long)(totalMinutes % 60); // 총 근무시간(분)
		String totalWorkTime = hours + "시간 " + minutes + "분"; // 총 근무시간 문자열
		
		// 요일별 평균 근무시간 계산
		Map<String,Double> weekdayAvg = new LinkedHashMap<>();
		// workTimePerWeekday.keySet() -> 요일별로 저장된 Map의 키값(요일) 반환
		for (String day : workTimePerWeekday.keySet()) {
			// 해당 요일의 근무시간 리스트 가져오기
			List<Double> workTimes = workTimePerWeekday.get(day);
			// 근무시간이 없으면 평균 0 , 있으면 평균 계산
			double sum = 0;
			for (Double time : workTimes) {
				sum += time;
			}
			// 평균 계산 : 총합 / 데이터 개수
			double avg = sum / workTimes.size();
			// 결과에 요일별 평균 저장
			weekdayAvg.put(day, avg);
		}
		
		// 평균 출근 시간 계산
		String averageCheckInTime = "";
		if(!checkInTimes.isEmpty()) {
			int totalCheckInMinutes = 0;
			for (LocalTime time : checkInTimes) {
				totalCheckInMinutes += time.getHour() * 60 + time.getMinute(); // 시 * 60 + 분
			}
			int avgMinutes = totalCheckInMinutes / checkInTimes.size(); // 평균 시
			int avgHours = avgMinutes / 60; // 평균 시
			int avgMinutesRemain = avgMinutes % 60; // 평균 분
			averageCheckInTime = String.format("%02d:%02d", avgHours, avgMinutesRemain); // 시:분 형식으로 변환
		}
		
		// 출근률 계산
		int weekdayCount = 0; // 주말 제외 출근일 수
		LocalDate startDate = currentMonth.atDay(1); // 이번 달 첫날
		LocalDate endDate = currentMonth.atEndOfMonth(); // 이번 달 마지막 날
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) { // 이번 달 날짜 반복
			// 토요일, 일요일 제외
			if (date.getDayOfWeek().getValue() < 6) { // 월~금
				weekdayCount++;
			}	
		}
		
		String attendanceRate = "";
		if(weekdayCount > 0 ) {
			double rate = (checkinDays * 100.0) / weekdayCount; // 출근일 수 / 주말 제외 출근일 수
			attendanceRate = String.format("%.0f", rate) + "%"; // 소수점 0자리까지
			
		}
		
		// 결과 Map 생성
		Map<String,Object> result = new HashMap<>();
		result.put("checkinDays", checkinDays); // 출근일 수
		result.put("lateCount", lateCount); // 지각 횟수
		result.put("earlyLeaveCount", earlyLeaveCount); // 조퇴 횟수
		result.put("workTimePerDay", workTimePerDay); // 일별 근무시간
		result.put("weekdayAvg", weekdayAvg); // 요일별 평균 근무시간
		result.put("totalWorkTime", totalWorkTime); // 총 근무시간
		result.put("averageCheckInTime", averageCheckInTime); // 평균 출근시간
		result.put("attendanceRate", attendanceRate); // 출근률
		
		return result;
	}
	

}
