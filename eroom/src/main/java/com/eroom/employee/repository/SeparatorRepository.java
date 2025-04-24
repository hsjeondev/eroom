package com.eroom.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.employee.entity.Separator;

public interface SeparatorRepository extends JpaRepository<Separator, String> {
    Optional<Separator> findBySeparatorName(String name); // 예: "개인"
}
