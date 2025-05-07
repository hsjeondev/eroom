package com.eroom.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.employee.entity.Employee;
import com.eroom.project.entity.Project;
import com.eroom.project.entity.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long>, JpaSpecificationExecutor<ProjectMember> {

	List<ProjectMember> findByProject_ProjectNo(Long project_no);

	@Query("SELECT pm.employee FROM ProjectMember pm WHERE pm.project.projectNo = :projectNo")
	List<Employee> findEmployeesByProjectNo(@Param("projectNo") Long projectNo);
	
	List<ProjectMember> findByEmployeeEmployeeNo(Long employeeNo);
	
	@Query("SELECT COUNT(DISTINCT pm.project.projectNo) " +
		       "FROM ProjectMember pm " +
		       "WHERE pm.employee.employeeNo = :employeeNo " +
		       "AND pm.project.proceed = :status " +
		       "AND pm.project.visibleYn = 'Y'")
		int countMyProjectsByStatus(@Param("employeeNo") Long employeeNo,
		                            @Param("status") String status);


	
}
