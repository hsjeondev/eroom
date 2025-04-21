package com.eroom.calendar.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	    List<CompanyCalendar> list = companyRepository.findBySeparatorAndVisibleYn(separator,"Y");
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
	
	public CompanyCalendarDto deleteCompanyCalendar(Long id) {
		CompanyCalendar target = companyRepository.findById(id).orElse(null);
		if(target == null) {
			return null;
		}
		
		 String changeYtoN = "Y".equals(target.getVisibleYn()) ? "N" : "Y";
	    
	    CompanyCalendar updated = CompanyCalendar.builder()
	    	    .calendarNo(target.getCalendarNo())  
	    	    .companyTitle(target.getCompanyTitle())
	    	    .companyLocation(target.getCompanyLocation())
	    	    .calendarStartTime(target.getCalendarStartTime())
	    	    .calendarEndTime(target.getCalendarEndTime())
	    	    .companyContent(target.getCompanyContent())
	    	    .companyCreator(target.getCompanyCreator())
	    	    .companyEditor(target.getCompanyEditor())
	    	    .employeeNo(target.getEmployeeNo())
	    	    .separator(target.getSeparator())
	    	    .calendarRegDate(target.getCalendarRegDate())
	    	    .calendarModDate(target.getCalendarModDate())
	    	    .visibleYn(changeYtoN)
	    	    .build();
	    CompanyCalendar saved = companyRepository.save(updated);
	    return new CompanyCalendarDto().toDto(saved);
	}
	
	public List<Map<String, Object>> getAllVisibleCalendars() {
	    List<CompanyCalendar> list = companyRepository.findBySeparatorStartingWithAndVisibleYn("A", "Y");

	    List<Map<String, Object>> result = new ArrayList<>();
	    for (CompanyCalendar entity : list) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("calendar_no", entity.getCalendarNo());
	        map.put("title", entity.getCompanyTitle());
	        map.put("start", entity.getCalendarStartTime().toString());
	        map.put("end", entity.getCalendarEndTime().toString());
	        map.put("location", entity.getCompanyLocation());
	        map.put("description", entity.getCompanyContent());
	        map.put("separator", entity.getSeparator());

	        result.add(map); // ✅ 올바른 리스트 추가!
	    }
	    return result;
	}
}
