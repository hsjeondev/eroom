package com.eroom.employee.entity;

import java.time.LocalDateTime;
import java.util.List;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long structureNo;
	
	@Column(name="separator_code")
	private String separatorCode;
	
	@Column(name="code_name")
	private String codeName;
	
	@Column(name="sort_order")
	private Long sortOrder;
	
	@Column(name="parent_code")
	private String parentCode;
	
	@Column(name="visible_yn")
	private String visibleYn;
	
	@Column(name="creator")
	private String creator;
	@Column(name="editor")
	private String editor;
	
	// DB에 맡김. 값만 꺼내 쓰기 위해 필드 명시.
	@Column(name="reg_date", insertable = false, updatable = false)
	private LocalDateTime regDate;
	@Column(name="mod_date", insertable = false, updatable = false)
	private LocalDateTime modDate;
	
	
	@OneToMany(mappedBy = "structure")
	@ToString.Exclude
	private List<Employee> employees;
	
	
	
}
