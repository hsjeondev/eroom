//package com.eroom.attendance.entity;
//
//import java.time.LocalDateTime;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Table(name = "holiday")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Holiday {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "holiday_no")
//	private Long holidayNo; // 공휴일 번호
//	
//	@Column(name = "holiday_date", nullable = false, unique = true)
//	private LocalDateTime holidayDate; // 공휴일 날짜 (중복 방지 unique)
//	
//	@Column(name = "holiday_name", nullable = false)
//	private String holidayName; // 공휴일 이름
//	
//	@Column(name = "holiday_year", nullable = false)
//	private int holidayYear; // 연도
//	
//	@Column(name = "holiday_reg_date")
//	private LocalDateTime holidayRegDate; // 등록일시
//}
