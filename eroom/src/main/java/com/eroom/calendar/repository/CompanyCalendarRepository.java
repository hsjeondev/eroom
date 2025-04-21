package com.eroom.calendar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.eroom.calendar.entity.CompanyCalendar;


public interface CompanyCalendarRepository extends JpaRepository<CompanyCalendar,Long>,JpaSpecificationExecutor<CompanyCalendar> {
	List<CompanyCalendar> findBySeparatorAndVisibleYn(String separator,String visibleYn);
}
