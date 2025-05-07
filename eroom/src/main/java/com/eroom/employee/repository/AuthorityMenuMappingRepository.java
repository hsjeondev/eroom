package com.eroom.employee.repository;

import com.eroom.employee.entity.AuthorityMenuMapping;
import com.eroom.employee.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityMenuMappingRepository extends JpaRepository<AuthorityMenuMapping, Long> {
    
    // Authority를 기준으로 AuthorityMenuMapping을 가져오는 메서드
    List<AuthorityMenuMapping> findByAuthority(Authority authority);
}
