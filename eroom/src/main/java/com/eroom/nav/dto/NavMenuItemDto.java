package com.eroom.nav.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class NavMenuItemDto {
    private Long id;
    private Long parentId;
    private String label;
    private String url;
    private String iconClass;
    private String collapseTarget;
    private List<NavMenuItemDto> children = new ArrayList<>();

    /**
     * 편의 생성자: 하위 메뉴(children) 없이 빠르게 생성하고 싶을 때
     */
    public NavMenuItemDto(Long id,
                          Long parentId,
                          String label,
                          String url,
                          String iconClass,
                          String collapseTarget) {
        this.id = id;
        this.parentId = parentId;
        this.label = label;
        this.url = url;
        this.iconClass = iconClass;
        this.collapseTarget = collapseTarget;
    }

    public NavMenuItemDto addChild(NavMenuItemDto child) {
        this.children.add(child);
        return this;
    }
}
