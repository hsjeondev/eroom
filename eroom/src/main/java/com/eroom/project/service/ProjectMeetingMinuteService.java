package com.eroom.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.project.dto.ProjectMeetingMinuteDto;
import com.eroom.project.entity.ProjectMeetingMinute;
import com.eroom.project.repository.ProjectMeetingMinuteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectMeetingMinuteService {

    private final ProjectMeetingMinuteRepository projectMeetingMinuteRepository;

    public List<ProjectMeetingMinuteDto> getMeetingMinutesByProject(Long projectNo) {
        List<ProjectMeetingMinute> entityList = projectMeetingMinuteRepository.findByProjectNo(projectNo);
        List<ProjectMeetingMinuteDto> dtoList = new ArrayList<>();

        for (ProjectMeetingMinute entity : entityList) {
            dtoList.add(ProjectMeetingMinuteDto.toEntity(entity));
        }

        return dtoList;
    }

}
