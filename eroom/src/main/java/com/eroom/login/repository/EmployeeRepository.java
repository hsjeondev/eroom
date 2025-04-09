package com.eroom.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.directory.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

//	Optional<Employee> findByEmployeeId(String employeeId);
	
	@Query("SELECT e FROM Employee e LEFT JOIN FETCH e.authorities WHERE e.employeeId = :employeeId")
	Optional<Employee> findByEmployeeId(@Param("employeeId") String employeeId);
}
