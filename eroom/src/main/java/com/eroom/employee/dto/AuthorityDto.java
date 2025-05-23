package com.eroom.employee.dto;

import java.util.List;

import com.eroom.employee.entity.Authority;
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
public class AuthorityDto {
    private Long authority_no;
    private String authority_name;
    private List<AuthorityMenuMapping> authorityMenuMappings; // 메뉴 매핑 추가

    // Authority 엔티티를 AuthorityDto로 변환
    public AuthorityDto toDto(Authority entity) {
        return AuthorityDto.builder()
                .authority_no(entity.getAuthorityNo())
                .authority_name(entity.getAuthorityName())
                .authorityMenuMappings(entity.getAuthorityMenuMappings()) // 메뉴 매핑 추가
                .build();
    }

    // AuthorityDto를 Authority 엔티티로 변환
    public Authority toEntity() {
        return Authority.builder()
                .authorityNo(authority_no)
                .authorityName(authority_name)
                .build();
    }
}
