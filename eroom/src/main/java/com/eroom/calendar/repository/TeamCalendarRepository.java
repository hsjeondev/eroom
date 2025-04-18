package com.eroom.calendar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.calendar.entity.TeamCalendar;

public interface TeamCalendarRepository extends JpaRepository<TeamCalendar,Long>,JpaSpecificationExecutor<TeamCalendar> {
	List<TeamCalendar> findBySeparatorAndVisibleYn(String separator,String visibleYn);
	
	@Query("SELECT c FROM TeamCalendar c WHERE c.separator LIKE CONCAT(:departmentCode, '%') AND c.visibleYn = 'Y'")
	List<TeamCalendar> findTeamSchedulesByDepartmentCode(@Param("departmentCode") String departmentCode);

}
