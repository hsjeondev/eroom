package com.eroom.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.eroom.calendar.entity.EmployeeCalendar;

public interface EmployeeCalendarRepository extends JpaRepository<EmployeeCalendar,Long>,JpaSpecificationExecutor<EmployeeCalendar> {

}
