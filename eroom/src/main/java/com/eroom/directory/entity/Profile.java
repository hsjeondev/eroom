
package com.eroom.directory.entity;


import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name="profile")
public class Profile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long profileNo; // 프로필 번호
	
	@Column(name = "profile_ori_name")
	private String profileOriName;
	@Column(name = "profile_new_name")
	private String profileNewName;
	@Column(name = "profile_path")
	private String profilePath;
	
	@OneToOne
	@JoinColumn(name="employee_no")
	private Employee employeeNo;

	
}

