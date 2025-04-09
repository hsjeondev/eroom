package com.eroom.authority.dto;



import com.eroom.authority.entity.Authority;
import com.eroom.authority.entity.AuthorityMenu;
import com.eroom.authority.entity.AuthorityMenuMapping;

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

	private Long authority_menu_mapping_no;
	private Long authority_no;
	private Long authority_menu_no;
	
	public AuthorityMenuMapping toEntity() {
		return AuthorityMenuMapping.builder()
				.authorityMenuMappingNo(authority_no)
				.authority(Authority.builder().authorityNo(authority_menu_mapping_no).build())
				.authorityMenu(AuthorityMenu.builder().authorityMenuNo(authority_menu_mapping_no).build())
				.build();
	}
	
	public AuthorityMenuMappingDto toDto(AuthorityMenuMapping entity) {
		return AuthorityMenuMappingDto.builder()
				.authority_menu_mapping_no(entity.getAuthorityMenuMappingNo())
				.build();
	}
	
}
