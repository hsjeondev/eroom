package com.eroom.report.service;

import static net.sf.dynamicreports.report.builder.DynamicReports.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.eroom.attendance.dto.AttendanceDto;

import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;

@Service
public class ReportService {
	
	// 근태 기록 엑셀 리포트 생성용
	public ByteArrayOutputStream generateAttendanceExcelReport(List<AttendanceDto> records, String month) throws DRException{
		// 엑셀 데이터를 담을 출력 스트림 생성
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 데이터 소스 컬럼 정의
		DRDataSource dataSource = new DRDataSource("no", "date", "checkin", "checkout", "late", "early");
		int i = 1;
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		// 데이터 소스에 행 추가 - 실제 데이터 넣기
		for(AttendanceDto dto : records) {
			LocalDateTime checkInTime = dto.getAttendance_check_in_time();
			LocalDateTime checkOutTime = dto.getAttendance_check_out_time();
			// 각 컬럼별 텍스트 가공
//			String date = checkInTime != null ? checkInTime.toLocalDate().format(dateFormatter) : "-";
			String date;
			if(checkInTime != null) {
				String baseDate = checkInTime.toLocalDate().format(dateFormatter); // 날짜
				String dayOfWeek = checkInTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN); // 요일 한국어 변환
				date = baseDate + " (" + dayOfWeek + ")"; // 날짜(요일) 
			}else {
				date = "-";
			}
			String checkin = checkInTime != null ? checkInTime.toLocalTime().format(timeFormatter) : "-";
			String checkout = checkOutTime != null ? checkOutTime.toLocalTime().format(timeFormatter) : "-";
			String late = "Y".equals(dto.getAttendance_late_yn()) ? "지각" : "";
			String early = "Y".equals(dto.getAttendance_early_leave_yn()) ? "조퇴" : "";
			// 행 데이터 추가
			dataSource.add(i++, date, checkin, checkout, late, early);
		}
		// 기본 셀 스타일 정의
		StyleBuilder baseStyle = stl.style()
					.setFont(
							stl.font() // 한글 폰트 설정
							.setFontName("Malgun Gothic") // 맑은 고딕
							.setFontSize(10) // 글자 크기
					 )
					.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER) // 글자 가운데 정렬(가로 가운데 정렬)
					.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE) // 셀 높이 대비 중앙 정렬(세로 가운데 정렬)
					.setBorder(stl.pen1Point()) // 셀 테두리
					.setPadding(3); // 셀 안 여백
		// 타이틀 텍스트 스타일 (제목 영역)
		StyleBuilder titleStyle = stl.style(baseStyle).bold() // 굵은 글씨
							.setFontSize(12).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		// 컬럼 제목 스타일
		StyleBuilder columnHeaderStyle = stl.style(baseStyle).bold().setBackgroundColor(Color.LIGHT_GRAY);

		// 리포트 생성
		report()
	    .setColumnStyle(baseStyle) 
	    .setColumnTitleStyle(columnHeaderStyle) // 컬럼과 내용 기본 스타일 적용
	    .addTitle( // 리포드 상단 타이틀 설정
			Components.text(month + " 근태기록")
				.setStyle(titleStyle) // 타이틀 스타일 적용
				.setFixedHeight(30)
		)
	    .columns( // 표 컬럼 정의
	        Columns.column("번호", "no", DataTypes.integerType()),
	        Columns.column("근무일", "date", DataTypes.stringType()),
	        Columns.column("출근 시간", "checkin", DataTypes.stringType()),
	        Columns.column("퇴근 시간", "checkout", DataTypes.stringType()),
	        Columns.column("지각", "late", DataTypes.stringType()),
	        Columns.column("조퇴", "early", DataTypes.stringType())
	    )
	    .setDataSource(dataSource) // 데이터 바인딩
	    .toXls(baos); // Excel로 출력

			return baos;
	}


}