package com.eroom.facility.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "facility")
@Builder
@Getter
@ToString
public class Facility {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "facility_no")
	private Long facilityNo;
	
	@Column(name = "separator_code")
	private String separatorCode;
	
	@Column(name = "visible_yn")
	@Builder.Default
	private String visibleYn="Y";
	
	@Column(name = "facility_creator")
	private String facilityCreator;
	
	@Column(name = "facility_editor")
	private String facilityEditor;
	
	@Column(updatable = false, name = " facility_reg_date")
	@CreationTimestamp
	private LocalDateTime facilityRegDate;
	
	@UpdateTimestamp
	@Column(insertable = false, name = "facility_mod_date")
	private LocalDateTime facilityModDate;
	
	@Column(name = "facility_name")
	private String facilityName;
	
	@Column(name = "facility_capacity")
	private String facilityCapacity;
	
	
	
	
	

}
