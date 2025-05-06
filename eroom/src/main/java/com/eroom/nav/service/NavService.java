package com.eroom.nav.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.eroom.employee.entity.Authority;
import com.eroom.employee.entity.AuthorityMenuMapping;
import com.eroom.employee.repository.AuthorityMenuMappingRepository;
import com.eroom.nav.dto.NavMenuItemDto;
import com.eroom.nav.entity.NavMenuItem;
import com.eroom.nav.repository.NavMenuItemRepository;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NavService {

    private final NavMenuItemRepository navMenuItemRepository;
    private final AuthorityMenuMappingRepository authorityMenuMappingRepository; // 권한과 메뉴 항목을 매핑하는 리포지토리

    // @AuthenticationPrincipal을 사용하여 인증된 사용자 정보를 직접 주입받음
    public List<NavMenuItemDto> getHierarchicalNav(EmployeeDetails user) {
        // 로그인한 사용자의 권한 정보를 가져옵니다.
        List<Authority> authorities = user.getEmployee().getAuthorities(); // 로그인한 사용자의 권한 리스트

        // 사용자의 권한에 해당하는 메뉴 항목을 가져옵니다.
        List<Long> allowedMenuIds = new ArrayList<>();
        for (Authority authority : authorities) {
            // 권한에 해당하는 메뉴 항목을 가져옵니다.
            List<AuthorityMenuMapping> mappings = authorityMenuMappingRepository.findByAuthority(authority);
            for (AuthorityMenuMapping mapping : mappings) {
                allowedMenuIds.add(mapping.getNavMenuItem().getId());
            }
        }

        // 1. 모든 메뉴를 가져옵니다.
        List<NavMenuItem> all = navMenuItemRepository.findByVisibleYnOrderByParentIdAscOrderIndexAsc("Y");

        // 2. 모든 메뉴를 DTO로 매핑
        Map<Long, NavMenuItemDto> dtoMap = new LinkedHashMap<>();
        for (NavMenuItem menu : all) {
            // 권한에 맞는 메뉴 항목만 필터링
            if (allowedMenuIds.contains(menu.getId())) {
                NavMenuItemDto dto = new NavMenuItemDto(
                        menu.getId(),
                        menu.getParentId(),
                        menu.getLabel(),
                        menu.getUrl(),
                        menu.getIconClass(),
                        menu.getCollapseTarget()
                );
                dtoMap.put(menu.getId(), dto);
            }
        }

        // 3. 계층 구조 구성
        List<NavMenuItemDto> rootList = new ArrayList<>();
        for (NavMenuItem menu : all) {
            Long parentId = menu.getParentId();
            NavMenuItemDto current = dtoMap.get(menu.getId());

            if (parentId == null) {
                rootList.add(current);
            } else {
                NavMenuItemDto parent = dtoMap.get(parentId);
                if (parent != null) {
                    parent.addChild(current);
                }
            }
        }
        
        System.out.println("rootList " + rootList);

        return rootList;
    }
}
