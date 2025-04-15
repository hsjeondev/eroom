package com.eroom.reporttest.service;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;

@Service
public class ReportServiceTest {

    public ByteArrayOutputStream generateCenteredPdfReport() throws DRException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // 기본 중앙 정렬 스타일(글자 가운데 정렬, 한국어 폰트 적용)
        StyleBuilder centerStyle = stl.style()
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setFont(stl.font().setFontName("Malgun Gothic"));
        
        // 여기서 테두리를 추가한 스타일을 생성 (pen1Point() 사용: 1포인트 검은색 선)
        StyleBuilder tableCellStyle = stl.style(centerStyle).setBorder(stl.pen1Point());
        
        // 타이틀 스타일 (가운데 정렬, 굵게, 폰트 크기 16)
        StyleBuilder titleStyle = centerStyle.bold().setFontSize(16);
        // 헤더 스타일 (가운데 정렬, 굵게, 폰트 크기 12)
        StyleBuilder headerStyle = centerStyle.bold().setFontSize(12);
        
        // 데이터 소스 구성: dummy 컬럼을 이용하여 전체 테이블이 가운데 위치하도록 함.
        // 컬럼: emptyLeft, name, position, emptyRight
        DRDataSource dataSource = new DRDataSource("emptyLeft", "name", "position", "emptyRight");
        dataSource.add("", "전홍식", "하면해", "");
        dataSource.add("", "이예준", "응애", "");
        dataSource.add("", "강성관", "닫은 식당", "");
        dataSource.add("", "조윤아", "으앙", "");
        dataSource.add("", "권용규", "국쏘?", "");
        dataSource.add("", "조은성", "연식당", "");
        
        // 페이지 총 폭 555에 맞추어 dummy 컬럼과 실제 데이터 컬럼의 폭을 설정
        int leftDummyWidth = 127;
        int nameWidth = 200;
        int positionWidth = 100;
        int rightDummyWidth = 128;
        
        report()
            .setPageMargin(margin(20))
            // dummy 컬럼을 이용해 전체 컬럼이 가운데 위치하도록 구성
            .columns(
                // 왼쪽 빈 컬럼
                Columns.column("", "emptyLeft", type.stringType()).setFixedWidth(leftDummyWidth),
                // 실제 데이터 컬럼: 이름 (중앙 정렬, 테두리 포함)
                Columns.column("이름", "name", type.stringType())
                        .setFixedWidth(nameWidth)
                        .setStyle(tableCellStyle)
                        .setTitleStyle(headerStyle),
                // 실제 데이터 컬럼: 역할 (중앙 정렬, 테두리 포함)
                Columns.column("역할", "position", type.stringType())
                        .setFixedWidth(positionWidth)
                        .setStyle(tableCellStyle)
                        .setTitleStyle(headerStyle),
                // 오른쪽 빈 컬럼
                Columns.column("", "emptyRight", type.stringType()).setFixedWidth(rightDummyWidth)
            )
            // 타이틀을 추가 (가운데 정렬)
            .title(Components.text("샘플 리포트")
                    .setStyle(titleStyle)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER))
            .pageFooter(Components.pageXofY())
            .setDataSource(dataSource)
            .toPdf(baos);
        
        return baos;
    }
}
