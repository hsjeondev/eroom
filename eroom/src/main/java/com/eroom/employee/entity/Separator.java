package com.eroom.employee.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.eroom.directory.entity.Directory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name="`separator`")
public class Separator {
	
	@Id
	@Column(name="separator_code")
	private String separatorCode;
	
	@Column(name="separator_parent_code")
	private String separatorParentCode;
	@Column(name="separator_name")
	private String separatorName;
	@Column(name="separator_yn")
	private String separatorYn;
	@Column(name="separator_creator")
	private String separatorCreator;
	@Column(name="separator_editor")
	private String separatorEditor;
	
	@Column(name="separator_reg_date", insertable = false, updatable = false)
	private LocalDateTime separatorRegDate;
	@Column(name="separator_mod_date", insertable = false, updatable = false)
	private LocalDateTime separatorModDate;
	
	@OneToMany(mappedBy = "separator")
	private List<Directory> directories;

}
