package com.eroom.birt.test;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

public class BirtPdfExporter {

    public static void main(String[] args) throws Exception {
        // BIRT 로그 안 뜨게 하기
        Logger.getLogger("org.eclipse.birt").setLevel(Level.SEVERE);

        // 1. 엔진 설정
        EngineConfig config = new EngineConfig();
        config.setEngineHome("birt-runtime-directory"); // 생략 가능
        Platform.startup(config);

        // 2. 엔진 팩토리 생성
        IReportEngineFactory factory = (IReportEngineFactory) Platform
                .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        IReportEngine engine = factory.createReportEngine(config);

        // 3. 리포트 실행용 객체 생성
        IReportRunnable design = engine.openReportDesign("example.rptdesign");

        IRunAndRenderTask task = engine.createRunAndRenderTask(design);
        task.setParameterValues(new HashMap<>()); // 파라미터가 있다면 여기에 설정

        // 4. PDF 설정
        PDFRenderOption options = new PDFRenderOption();
        options.setOutputFileName("output.pdf");
        options.setOutputFormat("pdf");
        task.setRenderOption(options);

        // 5. 실행
        task.run();
        task.close();
        engine.destroy();
        Platform.shutdown();

        System.out.println("PDF 생성 완료!");
    }
}

