package com.eroom.nav.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eroom.nav.entity.NavMenuItem;

public interface NavMenuItemRepository extends JpaRepository<NavMenuItem, Long> {

    // visible_yn이 'Y'인 항목만 parentId, orderIndex 순으로 정렬해서 조회
    List<NavMenuItem> findByVisibleYnOrderByParentIdAscOrderIndexAsc(String visibleYn);
}
