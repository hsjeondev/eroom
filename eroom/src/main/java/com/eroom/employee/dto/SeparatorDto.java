package com.eroom.employee.dto;

import java.time.LocalDateTime;

import com.eroom.employee.entity.Separator;

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
public class SeparatorDto {
	private String separator_code;
	private String separator_parent_code;
	private String separator_name;
	private String separator_yn;
	private String separator_creator;
	private String separator_editor;
	private LocalDateTime separator_reg_date;
	private LocalDateTime separator_mod_date;
	
	public Separator toEntity() {
		return Separator.builder()
				.separatorCode(separator_code)
				.separatorParentCode(separator_parent_code)
				.separatorName(separator_name)
				.separatorYn(separator_yn)
				.separatorCreator(separator_creator)
				.separatorEditor(separator_editor)
				.separatorRegDate(separator_reg_date)
				.separatorModDate(separator_mod_date)
				.build();
	}
	public SeparatorDto toDto(Separator entity) {
		return SeparatorDto.builder()
				.separator_code(entity.getSeparatorCode())
				.separator_parent_code(entity.getSeparatorParentCode())
				.separator_name(entity.getSeparatorName())
				.separator_yn(entity.getSeparatorYn())
				.separator_creator(entity.getSeparatorCreator())
				.separator_editor(entity.getSeparatorEditor())
				.separator_reg_date(entity.getSeparatorRegDate())
				.separator_mod_date(entity.getSeparatorModDate())
				.build();
	}
	
}
