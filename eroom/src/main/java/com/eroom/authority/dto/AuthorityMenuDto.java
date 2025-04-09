package com.eroom.authority.dto;


import com.eroom.authority.entity.AuthorityMenu;

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
public class AuthorityMenuDto {
	private Long authority_menu_no;
	private String authority_menu_url;
	private String authority_menu_method;
	private String authority_menu_name;
	private String authority_menu_visible_yn;
	private Long authority_menu_no_parent;
	
	public AuthorityMenu toEntity() {
		return AuthorityMenu.builder()
				.authorityMenuNo(authority_menu_no)
				.authorityMenuMethod(authority_menu_method)
				.authorityMenuName(authority_menu_name)
				.authorityMenuVisibleYn(authority_menu_visible_yn)
				.authorityMenuNoParent(authority_menu_no_parent)
				.build();
	}
	public AuthorityMenuDto toDto(AuthorityMenu entity) {
		return AuthorityMenuDto.builder()
				.authority_menu_no(entity.getAuthorityMenuNo())
				.authority_menu_name(entity.getAuthorityMenuName())
				.authority_menu_method(entity.getAuthorityMenuMethod())
				.authority_menu_visible_yn(entity.getAuthorityMenuVisibleYn())
				.authority_menu_no_parent(entity.getAuthorityMenuNoParent())
				.build();
	}
}
