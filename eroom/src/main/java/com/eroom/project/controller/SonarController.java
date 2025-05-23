package com.eroom.project.controller;

import com.eroom.project.dto.QualityMeasureDto;
import com.eroom.project.service.SonarService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sonar/quality")
public class SonarController {

    private final SonarService sonarService;

    public SonarController(SonarService sonarService) {
        this.sonarService = sonarService;
    }

    // 프로젝트 전체(main 브랜치 기준) 코드 품질 메트릭을 조회
    @GetMapping
    public List<QualityMeasureDto> getProjectQuality() {
        return sonarService.getProjectQualityMeasures();
    }

    // 특정 PR 번호에 대한 품질 메트릭을 조회 (브랜치 분석 필요)
    @GetMapping("/pr/{prNumber}")
    public List<QualityMeasureDto> getPrQuality(@PathVariable("prNumber") String prNumber) {
        return sonarService.getPrQualityMeasures(prNumber);
    }
}
