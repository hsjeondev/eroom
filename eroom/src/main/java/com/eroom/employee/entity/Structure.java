package com.eroom.employee.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name="structure")
public class Structure {

	@Id
	@Column(name="structure_no")
	private Long structureNo;
	
	@Column(name="separator_code")
	private String separatorCode;
	
	@Column(name="code_name")
	private String codeName;
	
	@Column(name="order")
	private Long order;
	
	@Column(name="parent_code")
	private String parentCode;
	
	@Column(name="visible_yn")
	private String visibleYn;
	
	@OneToMany(mappedBy = "structure")
	@ToString.Exclude
	private List<Employee> emplyees;
	
}
