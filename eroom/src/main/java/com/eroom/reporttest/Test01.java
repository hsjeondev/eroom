package com.eroom.reporttest;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test01 {

    public static void main(String[] args) throws Exception {
        // 1. 리포트 디자인 로딩 (.jrxml → .jasper)
        JasperReport jasperReport = JasperCompileManager.compileReport("src/main/resources/reporttest/validated_sample_table_report.jrxml");

        // 2. 데이터 준비 (필드: name, age)
        List<Map<String, Object>> data = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("name", "홍길동");
        row1.put("age", 30);
        data.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("name", "이순신");
        row2.put("age", 45);
        data.add(row2);

        Map<String, Object> row3 = new HashMap<>();
        row3.put("name", "강감찬");
        row3.put("age", 50);
        data.add(row3);

        Map<String, Object> row4 = new HashMap<>();
        row4.put("name", "세종대왕");
        row4.put("age", 40);
        data.add(row4);

        Map<String, Object> row5 = new HashMap<>();
        row5.put("name", "유관순");
        row5.put("age", 20);
        data.add(row5);

        // 3. 데이터 소스 생성
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

        // 4. 파라미터 설정 (필요시 추가)
        Map<String, Object> params = new HashMap<>();

        // 5. 리포트 채우기
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        // 6. PDF 파일로 내보내기
        String dir = "C:/dynamicreporttest";
        new java.io.File(dir).mkdirs();  // 경로 없으면 생성

        String path = dir + "/output.pdf";
        try (FileOutputStream fos = new FileOutputStream(path)) {
            JasperExportManager.exportReportToPdfStream(jasperPrint, fos);
        }

        System.out.println("PDF 저장 완료: " + path);
    }
}
