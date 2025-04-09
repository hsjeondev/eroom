package com.eroom.directory.dto;

import com.eroom.directory.entity.Employee;
import com.eroom.directory.entity.Profile;

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
public class ProfileDto {
	
	private Long profile_no;
	private String profile_ori_name;
	private String profile_new_name;
	private String profile_path;
	private Long employee_no;
	
	public Profile toEntity() {
		return Profile.builder()
				.profileNo(profile_no)
				.profileOriName(profile_ori_name)
				.profileNewName(profile_new_name)
				.profilePath(profile_path)
				.employeeNo(Employee.builder().employeeNo(employee_no).build())
				.build();
	}
	
	public ProfileDto toDto(Profile pro) {
		return ProfileDto.builder()
				.profile_no(pro.getProfileNo())
				.profile_ori_name(pro.getProfileOriName())
				.profile_new_name(pro.getProfileNewName())
				.profile_path(pro.getProfilePath())
				.build();
	}
	
	
}
