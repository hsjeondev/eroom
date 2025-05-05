package com.eroom.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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

    private final ProjectMeetingMinuteRepository projectMeetingMinuteRepository;
    private final ProjectMeetingMinuteMappingRepository projectMeetingMinuteMappingRepository;

    public List<ProjectMeetingMinuteDto> getMeetingMinutesByProject(Long projectNo) {
        List<ProjectMeetingMinute> entityList = projectMeetingMinuteRepository.findByProjectNo(projectNo);
        List<ProjectMeetingMinuteDto> dtoList = new ArrayList<>();

        for (ProjectMeetingMinute entity : entityList) {
            dtoList.add(ProjectMeetingMinuteDto.toDto(entity));
        }

        return dtoList;
    }
    
    public Long saveMinute(ProjectMeetingMinuteDto dto) {
        ProjectMeetingMinute entity = dto.toEntity();
        ProjectMeetingMinute saved = projectMeetingMinuteRepository.save(entity);
        return saved.getMeetingMinuteNo();
    }
    
    public void saveMappings(ProjectMeetingMinuteMappingDto dto) {
        List<ProjectMeetingMinuteMapping> mappings = dto.toEntityList();
        projectMeetingMinuteMappingRepository.saveAll(mappings);
    }
}
