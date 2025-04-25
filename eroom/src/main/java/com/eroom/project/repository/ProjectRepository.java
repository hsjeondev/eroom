package com.eroom.project.repository;

import java.time.LocalDate;

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
		       " SET p.proceed = :proceed " +
		       " WHERE p.projectEnd = :projectEnd")
	int updateProceedByEndDate(@Param("proceed") String proceed, @Param("projectEnd") LocalDate projectEnd);
}
