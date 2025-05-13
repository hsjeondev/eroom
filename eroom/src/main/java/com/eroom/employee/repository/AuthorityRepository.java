package com.eroom.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.employee.entity.Authority;

public interface AuthorityRepository extends JpaRepository<Authority,Long>{

}
