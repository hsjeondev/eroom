package com.eroom.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.employee.entity.AuthorityMapping;

public interface AuthorityMappingRepository extends JpaRepository<AuthorityMapping,Long>{

}
