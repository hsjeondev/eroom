package com.eroom.reservation.entity;

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

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
@Builder
@Getter
public class MeetingRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reservation_no")
	private Long reservationNo;

	@Column(name = "facility_no")
	private Long facilityNo;

	@Column(name = "employee_no")
	private Long employeeNo;
	
	@Column(name = "separator_code")
	private String separatorCode;

	@Column(name = "reservation_start")
	private LocalDateTime reservationStart;

	@Column(name = "reservation_end")
	private LocalDateTime reservationEnd;

	@Builder.Default
	@Column(name = "`visible_yn`")
	private String visibleYn = "Y";
	
	@Column(name = "reservation_creator")
	private String reservationCreator;
	
	@Column(name = "reservation_editor")
	private String reservationEditor;
	
	@Column(name = "reservation_location")
	private String reservationLocation;
	
	@CreationTimestamp
	@Column(updatable = false, name = "reservation_reg_date")
	private LocalDateTime reservationRegDate;
	
	@UpdateTimestamp
	@Column(insertable = false, name = "reservation_mod_date")
	private LocalDateTime reservationModDate;

}
