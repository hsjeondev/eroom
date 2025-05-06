package com.eroom.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.project.entity.ProjectMeetingMinute;

public interface ProjectMeetingMinuteRepository extends JpaRepository<ProjectMeetingMinute, Long> {

    List<ProjectMeetingMinute> findByProjectNo(Long projectNo);
}
