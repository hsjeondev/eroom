package com.eroom.calendar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.calendar.entity.TeamCalendar;

public interface TeamCalendarRepository extends JpaRepository<TeamCalendar,Long>,JpaSpecificationExecutor<TeamCalendar> {
	List<TeamCalendar> findBySeparatorAndVisibleYn(String separator,String visibleYn);
	


}
