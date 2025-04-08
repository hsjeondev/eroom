package com.eroom.authority.dto;



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
	
	
}
