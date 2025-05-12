//package com.eroom.attendance.repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.eroom.attendance.entity.Holiday;
//
//public interface HolidayRepository extends JpaRepository<Holiday,Long>{
//
//	// 특정 날짜에 해당하는 공휴일이 이미 존재하는지 확인
//	boolean existsByHolidayDate(LocalDateTime holidayDate);
//	
//	// 특정 연도의 공휴일만 조회
//	List<Holiday> findByHolidayYear(int year);
//}
