package com.eroom.facility.dto;

import java.time.LocalDateTime;

import com.eroom.facility.entity.Facility;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class FacilityDto {
	private Long facility_no;
	private String separator_code;
	private String visible_yn = "Y";
	private String facility_creator;
	private String facility_editor;
	private LocalDateTime facility_reg_date;
	private LocalDateTime facility_mod_date;
	private String facility_name;
	private String facility_capacity;
	
//	public Facility toEntity() {
//		return Facility.builder()
//				.facilityNo(facility_no)
//				.separatorCode(separator_code)
//				.visibleYn(visible_yn)
//				.facilityCreator(facility_creator)
//				.facilityEditor(facility_editor)
//				.facilityName(facility_name)
//				.facilityCapacity(facility_capacity)
//				.build();
//	}
	public Facility toEntity() {
	    Facility facility = new Facility();
	    facility.setFacilityNo(this.facility_no);
	    facility.setFacilityName(this.facility_name);
	    facility.setFacilityCapacity(this.facility_capacity);
	    facility.setSeparatorCode(this.separator_code);
	    facility.setFacilityCreator(this.facility_creator); 
	    facility.setFacilityEditor(this.facility_editor);
	    return facility;
	}
	
	public FacilityDto toDto(Facility facility) {
		return FacilityDto.builder()
				.facility_no(facility.getFacilityNo())
				.separator_code(facility.getSeparatorCode())
				.visible_yn(facility.getVisibleYn())
				.facility_creator(facility.getFacilityCreator())
				.facility_editor(facility.getFacilityEditor())
				.facility_name(facility.getFacilityName())
				.facility_capacity(facility.getFacilityCapacity())
				.facility_reg_date(facility.getFacilityRegDate())
				.facility_mod_date(facility.getFacilityModDate())
				.build();
	}
}
