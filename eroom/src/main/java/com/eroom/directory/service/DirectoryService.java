package com.eroom.directory.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.eroom.directory.dto.DirectoryDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.repository.DirectoryRepository;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Separator;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.repository.StructureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DirectoryService {

	private final DirectoryRepository directoryRepository;

	// 직원 주소록 리스트 조회
	public List<Directory> selectDirectoryEmployeeAllBySeparatorCode() {
		List<Directory> list = directoryRepository.findBySeparator_SeparatorCode("A001");
		return list;
	}
	// 협력업체 주소록 리스트 조회
	public List<Directory> selectDirectoryPartnerAllBySeparatorCode() {
		List<Directory> list = directoryRepository.findBySeparator_SeparatorCode("A002");
		return list;
	}

	// 사번으로 디렉토리 정보 조회
	public Directory selectDirectoryByEmployeeNo(Long employeeNo) {
		return directoryRepository.findByEmployee_EmployeeNo(employeeNo);
	}
	// 협력업체 리스트
	public List<Directory> selectDirectoryPartner() {
		List<Directory> list = directoryRepository.findBySeparatorCode("A002");
		return list;
	}
	public int createPartner(Map<String, String> formData, Employee creator) {
		int result = 0;
	    try {
	    	String name = formData.get("name");
	    	String companyName = formData.get("companyName");
	    	String email = formData.get("email");
	    	String phone = formData.get("phone");
	    	String department = formData.get("department");
	    	String team = formData.get("team");
	    	String position = formData.get("position");
	    	
	    	DirectoryDto dto = DirectoryDto.builder()
	    			.directory_name(name)
	    			.directory_company_name(companyName)
	    			.directory_email(email)
	    			.directory_phone(phone)
	    			.directory_department(department)
	    			.directory_team(team)
	    			.directory_position(position)
	    			.separator_code("A002")
	    			.directory_creator(creator.getEmployeeId())
	    			.build();
	    	Directory entity = dto.toEntity2();
	    	directoryRepository.save(entity);
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
}
