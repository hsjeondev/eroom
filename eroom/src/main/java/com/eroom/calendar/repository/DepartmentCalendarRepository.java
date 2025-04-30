package com.eroom.calendar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.eroom.calendar.entity.CompanyCalendar;
import com.eroom.calendar.entity.DepartmentCalendar;

public interface DepartmentCalendarRepository
		extends JpaRepository<DepartmentCalendar, Long>, JpaSpecificationExecutor<DepartmentCalendar> {

	List<DepartmentCalendar> findBySeparatorInAndVisibleYn(List<String> separatorCodes, String visibleYn);
	
	List<DepartmentCalendar> findBySeparatorStartingWithAndVisibleYn(String prefix, String visibleYn);

}
