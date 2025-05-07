package com.eroom.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.project.entity.ProjectMeetingMinute;

public interface ProjectMeetingMinuteRepository extends JpaRepository<ProjectMeetingMinute, Long> {

	@Query("SELECT m FROM ProjectMeetingMinute m WHERE m.projectNo = :projectNo AND m.meetingMinuteVisible = 'Y'")
	List<ProjectMeetingMinute> findByProjectNo(@Param("projectNo") Long projectNo);

}
