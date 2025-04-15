package com.eroom.report.service;

import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;

@Service
public class ReportService {
	
	StyleBuilder koreanFont = stl.style()
			.setFont(stl.font()
					.setFontName("Malgun Gothic")
					.setPdfFontName("fonts/malgun.ttf")
					.setPdfEncoding("Identity-H")
					.setPdfEmbedded(true)
					);
	
	
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
				Columns.column("이름","name",type.stringType()),
				Columns.column("역할","position",type.stringType())
				
				)
			.title(Components.text("샘플 리포트").setStyle(koreanFont.bold().setFontSize(14)))
			.pageFooter(Components.pageXofY())
			.setDataSource(dataSource)
			.toPdf(baos);
		
		return baos;
		
		
		
	}

}
