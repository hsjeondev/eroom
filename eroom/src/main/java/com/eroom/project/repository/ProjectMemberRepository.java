package com.eroom.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.project.entity.Project;
import com.eroom.project.entity.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long>, JpaSpecificationExecutor<ProjectMember> {

	List<ProjectMember> findByProject_ProjectNo(Long project_no);
	
	// 진행 중인 프로젝트
//	@Query("SELECT pm.project FROM ProjectMember pm "
//			+ "WHERE pm.employee.employeeNo = :employeeNo "
//			+ "AND pm.visibleYn = 'Y' AND pm.project.visibleYn = 'Y' "
//			+ "AND pm.project.projectStart <= CURRENT_DATE "
//			+ "AND pm.project.projectEnd > CURRENT_DATE")
//	List<Project> findActiveProjectsByEmployeeNo(@Param("employeeNo") Long employeeNo);
//
//	// 완료된 프로젝트
//	@Query("SELECT pm.project FROM ProjectMember pm "
//			+ "WHERE pm.employee.employeeNo = :employeeNo "
//			+ "AND pm.visibleYn = 'Y' AND pm.project.visibleYn = 'Y' "
//			+ "AND pm.project.projectEnd < CURRENT_DATE")
//	List<Project> findCompletedProjectsByEmployeeNo(@Param("employeeNo") Long employeeNo);
	
	
}
