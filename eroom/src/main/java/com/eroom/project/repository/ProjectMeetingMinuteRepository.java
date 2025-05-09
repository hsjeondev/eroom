package com.eroom.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.project.entity.ProjectMeetingMinute;

public interface ProjectMeetingMinuteRepository extends JpaRepository<ProjectMeetingMinute, Long> {

	// 회의록 목록 조회
	@Query("SELECT m FROM ProjectMeetingMinute m WHERE m.projectNo = :projectNo AND m.meetingMinuteVisible = 'Y' ORDER BY m.meetingDate DESC")
	List<ProjectMeetingMinute> findByProjectNo(@Param("projectNo") Long projectNo);

	// 프로젝트 메인 최근 5개 회의록 조회
	@Query("SELECT m FROM ProjectMeetingMinute m WHERE m.projectNo = :projectNo AND m.meetingMinuteVisible = 'Y' ORDER BY m.meetingDate DESC")
	List<ProjectMeetingMinute> findTop5RecentMinuteByProject(@Param("projectNo") Long projectNo);

}
