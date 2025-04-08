package com.eroom.authority.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

@Entity
@Table(name="authority_menu")
public class AuthorityMenu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long authorityMenuNo;
	
	@Column(name = "authority_menu_url")
	private String authorityMenuUrl;
	@Column(name = "authority_menu_method")
	private String authorityMenuMethod;
	@Column(name = "authority_menu_name")
	private String authorityMenuName;
	@Column(name = "authority_menu_visible_yn")
	private String authorityMenuVisibleYn;
	@Column(name = "authority_menu_no_parent")
	private Long authorityMenuNoParent;
}
