package com.eroom.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.gpt.GptService;
import com.eroom.project.dto.ProjectMeetingMinuteDto;
import com.eroom.project.dto.ProjectMeetingMinuteMappingDto;
import com.eroom.project.entity.ProjectMeetingMinute;
import com.eroom.project.entity.ProjectMeetingMinuteMapping;
import com.eroom.project.repository.ProjectMeetingMinuteMappingRepository;
import com.eroom.project.repository.ProjectMeetingMinuteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectMeetingMinuteService {

	private final EmployeeRepository employeeRepository;
    private final ProjectMeetingMinuteRepository projectMeetingMinuteRepository;
    private final ProjectMeetingMinuteMappingRepository projectMeetingMinuteMappingRepository;
    private final GptService gptService;

    public List<ProjectMeetingMinuteDto> getMeetingMinutesByProject(Long projectNo) {
        List<ProjectMeetingMinute> entityList = projectMeetingMinuteRepository.findByProjectNo(projectNo);
        List<ProjectMeetingMinuteDto> dtoList = new ArrayList<>();

        for (ProjectMeetingMinute entity : entityList) {
            Long minuteNo = entity.getMeetingMinuteNo();
            int participantCount = (int) projectMeetingMinuteMappingRepository.countByMeetingMinuteNo(minuteNo); // ✅ 여기

            ProjectMeetingMinuteDto dto = ProjectMeetingMinuteDto.toDto(entity);
            dto.setParticipants(participantCount);

            dtoList.add(dto);
        }

        return dtoList;
    }

    
    @Transactional
    public Long saveMinuteAndMappings(ProjectMeetingMinuteDto minuteDto, List<Long> participants) {
        // 1. GPT 요약 요청
        String summary = gptService.summarizeMeetingContent(minuteDto.getMeetingContent());

        // 2. DTO에 요약 결과 세팅
        minuteDto.setMeetingContentSummary(summary);

        // 3. Entity로 변환 후 저장
        ProjectMeetingMinute entity = minuteDto.toEntity();
        ProjectMeetingMinute saved = projectMeetingMinuteRepository.save(entity);

        // 4. 참여자 매핑 저장
        ProjectMeetingMinuteMappingDto mappingDto = new ProjectMeetingMinuteMappingDto();
        mappingDto.setMeetingMinuteNo(saved.getMeetingMinuteNo());
        mappingDto.setParticipants(participants);
        List<ProjectMeetingMinuteMapping> mappings = mappingDto.toEntityList();
        projectMeetingMinuteMappingRepository.saveAll(mappings);

        return saved.getMeetingMinuteNo();
    }
    
    public ProjectMeetingMinuteDto findByMinuteNo(Long minuteNo) {
        ProjectMeetingMinute entity = projectMeetingMinuteRepository.findById(minuteNo).orElseThrow();
        return ProjectMeetingMinuteDto.toDto(entity);
    }

    public List<String> findParticipantNames(Long minuteNo) {
        List<ProjectMeetingMinuteMapping> mappings = projectMeetingMinuteMappingRepository.findByMeetingMinuteNo(minuteNo);
        List<Long> empNos = mappings.stream().map(ProjectMeetingMinuteMapping::getEmployeeNo).toList();

        List<String> names = new ArrayList<>();
        for (Long empNo : empNos) {
            employeeRepository.findById(empNo).ifPresent(emp -> names.add(emp.getEmployeeName()));
        }
        return names;
    }
    
    public List<Long> findParticipantNos(Long minuteNo) {
        return projectMeetingMinuteMappingRepository.findEmployeeNosByMeetingMinuteNo(minuteNo);
    }

    @Transactional
    public void updateMinute(Long meetingMinuteNo, String title, String content) {
        ProjectMeetingMinute entity = projectMeetingMinuteRepository.findById(meetingMinuteNo).orElse(null);

        entity.updateTitleAndContent(title, content);
    }

    @Transactional
    public void updateMappings(ProjectMeetingMinuteMappingDto mappingDto) {
        Long minuteNo = mappingDto.getMeetingMinuteNo();

        // 기존 매핑 삭제
        List<ProjectMeetingMinuteMapping> existing = projectMeetingMinuteMappingRepository.findByMeetingMinuteNo(minuteNo);
        projectMeetingMinuteMappingRepository.deleteAll(existing);

        // 새 매핑 저장
        List<ProjectMeetingMinuteMapping> newMappings = mappingDto.toEntityList();
        projectMeetingMinuteMappingRepository.saveAll(newMappings);
    }


	public void deleteMinute(Long minuteNo) {
	    // 원본 엔티티 조회
	    ProjectMeetingMinute entity = projectMeetingMinuteRepository.findById(minuteNo)
	        .orElse(null);

	    // DTO로 변환 후 visible 값을 "N"으로 설정
	    ProjectMeetingMinuteDto dto = ProjectMeetingMinuteDto.toDto(entity);
	    dto.setMeetingMinuteVisible("N");

	    // 다시 entity 변환 후 저장
	    projectMeetingMinuteRepository.save(dto.toEntity());
	}


	public boolean isMinuteParticipant(Long minuteNo, Long employeeNo) {
	    return projectMeetingMinuteMappingRepository.existsByMeetingMinuteNoAndEmployeeNo(minuteNo, employeeNo);
	}


	public String getSummaryByMinuteNo(Long minuteNo) {
	    ProjectMeetingMinute minute = projectMeetingMinuteRepository.findById(minuteNo).orElse(null);
	    return (minute != null) ? minute.getMeetingContentSummary() : "요약이 없습니다.";
	}

}
