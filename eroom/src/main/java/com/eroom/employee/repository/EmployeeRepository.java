package com.eroom.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.employee.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

//	Optional<Employee> findByEmployeeId(String employeeId);
	
	@Query("SELECT e FROM Employee e LEFT JOIN FETCH e.authorities WHERE e.employeeId = :employeeId")
	Optional<Employee> findByEmployeeId(@Param("employeeId") String employeeId);
	
	// 중복 제거 부서명 목록
	@Query("SELECT DISTINCT e.structure.codeName FROM Employee e")
	List<String> findDistinctStructureNames();
	
	// 특정 부서에 속한 직원
	List<Employee> findByStructure_CodeName(String codeName);


}
