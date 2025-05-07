package com.eroom.nav.entity;

import java.util.List;

import com.eroom.employee.entity.AuthorityMenuMapping;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="nav_menu_item")
public class NavMenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;  // 'label'을 'name'으로 변경
    
    @OneToMany(mappedBy = "navMenuItem")  // AuthorityMenuMapping과 연결
    private List<AuthorityMenuMapping> authorityMenuMappings;  // 해당 메뉴에 대한 권한 매핑들

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "url")
    private String url;

    @Column(name = "icon_class")
    private String iconClass;

    @Column(name = "order_index")
    private Long orderIndex;

    @Column(name = "collapse_target")
    private String collapseTarget;

    @Column(name = "visible_yn")
    private String visibleYn;
}
