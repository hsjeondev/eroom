package com.eroom.employee.dto;

import com.eroom.employee.entity.Authority;
import com.eroom.nav.entity.NavMenuItem;
import com.eroom.employee.entity.AuthorityMenuMapping;

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
@ToString
@Builder
public class AuthorityMenuMappingDto {

    private Long authorityMenuMappingNo;
    private Long authorityNo;
    private Long authorityMenuNo;

    // Entity로 변환하는 메서드
    public AuthorityMenuMapping toEntity() {
        return AuthorityMenuMapping.builder()
                .authorityMenuMappingNo(this.authorityMenuMappingNo)
                .authority(Authority.builder().authorityNo(this.authorityNo).build()) // authority_no로 Authority 설정
                .navMenuItem(NavMenuItem.builder().id(this.authorityMenuNo).build()) // authority_menu_no로 NavMenuItem 설정
                .build();
    }

    // DTO로 변환하는 메서드
    public static AuthorityMenuMappingDto toDto(AuthorityMenuMapping entity) {
        return AuthorityMenuMappingDto.builder()
                .authorityMenuMappingNo(entity.getAuthorityMenuMappingNo())
                .authorityNo(entity.getAuthority().getAuthorityNo()) // Authority의 authorityNo
                .authorityMenuNo(entity.getNavMenuItem().getId()) // NavMenuItem의 id
                .build();
    }
}
