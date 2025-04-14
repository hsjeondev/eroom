package com.eroom.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.project.dto.ProjectDto;
import com.eroom.project.entity.Project;
import com.eroom.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectDto> findAllProject() {
        List<Project> projects = projectRepository.findAll();
        List<ProjectDto> projectDtos = new ArrayList<>();

        for (Project project : projects) {
            projectDtos.add(new ProjectDto().toDto(project));
        }

        return projectDtos;
    }
    
    public Long getProjectCount() {
        return projectRepository.count();
    }
}
