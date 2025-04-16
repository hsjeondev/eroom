package com.eroom.calendar.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eroom.calendar.dto.CompanyCalendarDto;

import com.eroom.calendar.entity.CompanyCalendar;

import com.eroom.calendar.repository.CompanyCalendarRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyCalendarService {
	private final CompanyCalendarRepository companyRepository;
	
	//회사 캘린더 일정 등록
	public CompanyCalendarDto addCompanyCalendar(CompanyCalendarDto dto) {
		CompanyCalendar param = dto.toEntity();
		CompanyCalendar result = companyRepository.save(param);
		return new CompanyCalendarDto().toDto(result);
		
	}
	
	//회사 목록 일정 조회
	public List<CompanyCalendarDto> getCompanyList(String separator) {
	    List<CompanyCalendar> list = companyRepository.findBySeparator(separator);
	    List<CompanyCalendarDto> dtoList = new ArrayList<>();

	    for (CompanyCalendar companyCalendar : list) {
	        CompanyCalendarDto dto = new CompanyCalendarDto();
	        dtoList.add(dto.toDto(companyCalendar));
	    }

	    return dtoList;
	}
	
	//회사 특정 일정 단일 조회
	public CompanyCalendarDto findByCompanyNo(Long id) {
        // 데이터베이스에서 일정 가져오기
        CompanyCalendar calendar = companyRepository.findById(id).orElse(null);
        
        // 데이터가 없으면 null 반환
        if (calendar == null) {
            return null;
        }

        // EmployeeCalendarDto 객체 생성
        CompanyCalendarDto dto = new CompanyCalendarDto();
        
        // toDto 호출
        return dto.toDto(calendar);
    }
	
	//회사 일정 수정
	public CompanyCalendar updateCompanyCalendar(CompanyCalendarDto param) {
		CompanyCalendar result = null;
		CompanyCalendar target = companyRepository.findById(param.getCalendar_no()).orElse(null);
		if(target != null) {
	        param.setCompany_creator(target.getCompanyCreator());
				result = companyRepository.save(param.toEntity());
		}
		return result;
	}
	
	
}
