package com.eroom.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.project.entity.ProjectMeetingMinuteMapping;

public interface ProjectMeetingMinuteMappingRepository extends JpaRepository<ProjectMeetingMinuteMapping, Long> {

	long countByMeetingMinuteNo(Long meetingMinuteNo);

	List<ProjectMeetingMinuteMapping> findByMeetingMinuteNo(Long minuteMinuteNo);

	@Query("SELECT m.employeeNo FROM ProjectMeetingMinuteMapping m WHERE m.meetingMinuteNo = :minuteNo")
	List<Long> findEmployeeNosByMeetingMinuteNo(@Param("minuteNo") Long minuteNo);
}
