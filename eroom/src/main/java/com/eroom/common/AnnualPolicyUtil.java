package com.eroom.common;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
public class AnnualPolicyUtil {

	// 연차 기준 연도 계산 -> 테스트용 5월 12일
	public Long getTargetYearByPolicy() {
		LocalDate now = LocalDate.now();
		LocalDate grantDate = LocalDate.of(now.getYear(), 1, 1); // 연차 기준일
						//	= LocalDate.of(now.getYear(), 1, 1);
		// 1월 1일로 변경시 1 , 1 로 변경하면 됨
		return now.isBefore(grantDate) ? (long)(now.getYear()-1) : (long)now.getYear();
	}
}
