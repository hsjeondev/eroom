package com.eroom.authority.dto;

import com.eroom.authority.entity.Authority;

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
	
	public Authority toEntity() {
		return Authority.builder()
				.authorityNo(authority_no)
				.authorityName(authority_name)
				.build();
	}
	
	public AuthorityDto toDto(Authority entity) {
		return AuthorityDto.builder()
				.authority_no(entity.getAuthorityNo())
				.authority_name(entity.getAuthorityName())
				.build();
	}
}
