package com.eroom.authority.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="authority_menu_mapping")
public class AuthorityMenuMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long authorityMenuMapping;
	
	@ManyToOne
	@JoinColumn(name = "authority_no")
	private Authority authority;
	
	@ManyToOne
	@JoinColumn(name = "authority_menu_no")
	private AuthorityMenu authorityMenu;
	
	
	
}
