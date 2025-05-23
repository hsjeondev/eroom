package com.eroom.project.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

	@Modifying
	@Transactional
	@Query("UPDATE Project p " +
	       "SET p.proceed = :proceed " +
	       "WHERE p.projectEnd < :projectEnd AND p.proceed <> '보류'")
	int updateProceedByEndDate(@Param("proceed") String proceed,
	                            @Param("projectEnd") LocalDate projectEnd);

	
	List<Project> findByProceed(String proceed);

	@Query("SELECT COUNT(p) FROM Project p WHERE p.visibleYn = 'Y' AND p.proceed = :proceed")
	Long countByProceedVisibleOnly(@Param("proceed") String proceed);

	
	@Query("""
			SELECT p FROM Project p
			WHERE p.visibleYn = 'Y'
			AND (:proceed IS NULL OR p.proceed = :proceed)
			ORDER BY p.projectNo ASC
			""")
			List<Project> findByVisibleYnAndOptionalProceed(@Param("proceed") String proceed);
	
	@Query("SELECT COUNT(p) FROM Project p WHERE p.visibleYn = 'Y'")
	Long countVisibleProjects();
	
	@Query("SELECT p FROM Project p JOIN p.projectMembers m " +
		       "WHERE m.employee.employeeNo = :employeeNo " +
		       "AND p.proceed = :proceed " +
		       "AND p.visibleYn = 'Y'")
		List<Project> findByEmployeeAndProceed(@Param("employeeNo") Long employeeNo,
		                                       @Param("proceed") String proceed);




}
