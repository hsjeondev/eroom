package com.eroom.report.service;

import static net.sf.dynamicreports.report.builder.DynamicReports.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;

@Service
public class ReportService {
	
	
	StyleBuilder koreanFont = stl.style()
			.setFont(stl.font()
					.setFontName("Malgun Gothic") // 시스템 폰트명
					.setPdfFontName("fonts/malgun.ttf") // resources 기준 상대 경로
					.setPdfEncoding("Identity-H") // 유니코드
					.setPdfEmbedded(true) // PDF 내에 폰트 포함
					);
	
	StyleBuilder columnStyle = koreanFont
			.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER) // 셀 가운데 정렬
			.setBorder(stl.pen1Point()) // 테두리 추가
			.setPadding(5); // 셀 안쪽 여백
	
	StyleBuilder columnTitleStyle = columnStyle
									.bold()
									.setBackgroundColor(Color.LIGHT_GRAY); // 컬럼 제목 강조
	// Excel 리포트용 메소드
	public ByteArrayOutputStream generateStyledTableExcelReport() throws Exception{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// 한글 폰트 스타일 사용
		StyleBuilder koreanFont = stl.style()
				.setFont(stl.font()
				.setFontName("Malgun Gothic")
				.setPdfFontName("fonts/malgun.ttf") // Excel에서는 의미 없지만 스타일 통일
				.setPdfEncoding("Identity-H")
				.setPdfEmbedded(true)
			);
		
		StyleBuilder columnStyle = koreanFont
					.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
					.setBorder(stl.pen1Point())
					.setPadding(5);
		
		StyleBuilder columnTitleStyle = columnStyle
					.bold()
					.setBackgroundColor(Color.WHITE);
		
		DRDataSource dataSource = new DRDataSource("date","time");
		dataSource.add("25.04.15","14:00");
		dataSource.add("25.04.15","14:30");
		dataSource.add("25.04.15","15:00");
		dataSource.add("25.04.15","15:30");
		
		report()
			.columns(
					Columns.column("날짜","date",type.stringType())
					.setStyle(columnStyle)
					.setTitleStyle(columnTitleStyle),
					
					Columns.column("시간","time",type.stringType())
					.setStyle(columnStyle)
					.setTitleStyle(columnTitleStyle)
			)
			.title(
					Components.text("날짜 시간 리포트")
					.setStyle(koreanFont.bold().setFontSize(11))
					.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
			)
			.setDataSource(dataSource)
			.toXlsx(baos); // Excel로 출력
		
		return baos;
			
			
		
	}
	
	
	// PDF 리포트용 메소드
	public ByteArrayOutputStream generateSimplePdfReport() throws DRException{
	
		// PDF 데이터를 저장할 출력 스트림 생성
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// 리포트에 넣을 샘플 데이터를 정의함
		// DRDataSource는 컬럼명 정의 -> add()로 행을 추가한다.
		DRDataSource dataSource = new DRDataSource("name","position");
		dataSource.add("전홍식","하면해");	
		dataSource.add("이예준","응애");	
		dataSource.add("강성관","닫은 식당");	
		dataSource.add("조윤아","으앙");	
		dataSource.add("권용규","국쏘?");	
		dataSource.add("조은성","연식당");	
		
		// 리포트를 생성하는 main 코드 블록
		report()
			// 출력할 컬럼들을 정의 : 컬럼명, 필드명, 데이터 타입
			.columns(
				Columns.column("이름","name",type.stringType())
					.setStyle(columnStyle)
					.setTitleStyle(columnTitleStyle)
					.setFixedWidth(100),
				
				Columns.column("역할","position",type.stringType())
					.setStyle(columnStyle)
					.setTitleStyle(columnTitleStyle)
					.setFixedWidth(200)
				
				)
			.title(
					Components.text("샘플 리포트")
					.setStyle(koreanFont.bold().setFontSize(14))
					.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
					.setFixedWidth(300)
					)
			.pageFooter(Components.pageXofY().setStyle(koreanFont))
			.setDataSource(dataSource)
			.toPdf(baos);
		
		return baos;
		
		
		
	}

}
