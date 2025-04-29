package com.eroom.employee.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.admin.dto.CreateEmployeeDto;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.repository.DirectoryRepository;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.dto.StructureDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.employee.repository.SeparatorRepository;
import com.eroom.employee.repository.StructureRepository;
import com.eroom.security.EmployeeDetails;
import com.eroom.employee.entity.Separator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
	private final EmployeeRepository employeeRepository;
	private final StructureRepository structureRepository;
	private final DirectoryRepository employeeDirectoryRepository;
	private final SeparatorRepository separatorRepository;
	private final PasswordEncoder passwordEncoder;
	
	// 전체 직원 조회
	public List<Employee> findAllEmployee() {
		return employeeRepository.findAll();
	}
	
	// 중복 제거된 부서 이름 조회 후 DTO 목록 반환
	public List<SeparatorDto> findDistinctStructureNames() {
	    List<Structure> structures = employeeRepository.findDistinctStructures();
	    return structures.stream()
	        .map(entity -> SeparatorDto.builder()
	                .separator_code(entity.getSeparatorCode())
	                .separator_name(entity.getCodeName())
	                .build())
	        .collect(Collectors.toList());
	}
	
	public List<EmployeeDto> findEmployeesByStructureName(String separatorCode) {
	    // 부서명으로 직원을 조회
		System.out.println("현재 찾고 있는 부서 코드: " + separatorCode);
		List<Employee> employes = employeeRepository.findByStructure_SeparatorCode(separatorCode);
		List<EmployeeDto> employeeDtos = new ArrayList<>();
		for(Employee emp : employes) {
			EmployeeDto employeeDto = new EmployeeDto(emp.getEmployeeNo(), emp.getEmployeeName());
			employeeDtos.add(employeeDto);
		}
		return employeeDtos;
	}

	public List<EmployeeDto> findEmployeesByParentCode(String parentCode) {
	    // 부모 부서코드로 직원을 조회
		List<Employee> employes = employeeRepository.findByStructureParentCode(parentCode);
		List<EmployeeDto> employeeDtos = new ArrayList<>();
		for(Employee emp : employes) {
			EmployeeDto employeeDto = new EmployeeDto(emp.getEmployeeNo(), emp.getEmployeeName());
			employeeDtos.add(employeeDto);
		}
		return employeeDtos;
	}
	
	
	// 부서 하위의 모든 팀 조회
	public List<StructureDto> findTeams() {
		return structureRepository.findBySeparatorCodeStartingWith("T")
                .stream()
                .map(entity -> StructureDto.toDto(entity))
                .collect(Collectors.toList());
	}

	public List<Employee> findEmployeesByTeamId(String teamId) {
		return employeeRepository.findEmployeesByTeamId(teamId);
	}
	
	//부서만 조회
	public List<SeparatorDto> findOnlyDepartments() {
	    List<Structure> departments = structureRepository.findOnlyDepartments();

	    return departments.stream()
	        .map(entity -> SeparatorDto.builder()
	                .separator_code(entity.getSeparatorCode())
	                .separator_name(entity.getCodeName())
	                .build())
	        .collect(Collectors.toList());
	}
	
	// employeeNo로 직원 조회
	public Employee findEmployeeByEmployeeNo(Long employeeNo) {
		return employeeRepository.findById(employeeNo).orElse(null);
	}
	
	//  structureNo로 소속 직원 조회
	public List<Employee> findByStructureNo(Long structureNo){
		return employeeRepository.findByStructure_StructureNo(structureNo);
	}
	
	// 여러 structureNo에 속한 직원 일괄 조회
	public List<Employee> findByStructureNoIn(List<Long> structureNos){
		return employeeRepository.findByStructure_StructureNoIn(structureNos);
	}
	
	// 이름 중복 체크 false : 사용 가능한 이름
	public boolean existsByEmployeeName(String name) {
		return employeeRepository.existsByEmployeeName(name);
	}
	
	
	// 회원 생성
	@Transactional
	public void createEmployee(CreateEmployeeDto dto) {
		// employeeId 생성
		Long maxEmployeeNo = employeeRepository.findMaxEmployeeNo();
		if(maxEmployeeNo == null) {
			maxEmployeeNo = 0L; // 한 명도 없을 경우 0부터 시작
		}
		Long newEmployeeNo = maxEmployeeNo + 1; // emplyeeNo중 가장 큰 숫자에 +1
		String employeeId = String.format("%08d", newEmployeeNo); // employeeId를 8자리 문자열로 만들어줌
		
		// 생년월일 -> yyyyMMdd
		String birth = dto.getEmployee_birth().replaceAll("-", "");
		// 비밀번호 생성 -> 받은 생년월일을 암호화하여 비밀번호로 저장
		String encodedPw = passwordEncoder.encode(birth);
		
		// structure 가져오기
		Optional<Structure> optionalStructure = Optional.empty(); 
		if(dto.getTeam_no() != null) { // 팀 존재하면 팀으로 조회
			optionalStructure = structureRepository.findById(dto.getTeam_no());
		}else if(dto.getDepartment_no() != null) { // 부서로 조회
			optionalStructure = structureRepository.findById(dto.getDepartment_no());
		}
		
		Structure structure = optionalStructure.orElseThrow(() -> 
			new IllegalArgumentException("선택된 부서 또는 팀이 존재하지 않습니다.")
		);
		
		// Employee 엔티티 저장
		Employee employee = dto.toEmployeeEntity(employeeId, encodedPw, structure);
		employeeRepository.save(employee);
		
		
		// employeeNo 기반 이메일 생성
		Long employeeNo = employee.getEmployeeNo(); // 저장하면서 생성된 사번 가져오기
		String email = "employee" + employeeNo + "@eroom.com";
		
		// 현재 로그인한 사용자 정보 가져오기
		EmployeeDetails employeeDetail = (EmployeeDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String creatorName = employeeDetail.getEmployee().getEmployeeName(); // 주소록 생성한 이름
		
		// 부서명, 팀명 설정
		String departmentName = "-";
		String teamName = "-";
		
		if(structure != null) {
			if(structure.getParentCode() == null) {
				// 부모 코드가 없으면 부서
				departmentName = structure.getCodeName();
			}else {
				// 부모 코드가 있으면 팀
				teamName = structure.getCodeName();
				// 상위 부서 이름도 조회
				Structure parent = structureRepository.findBySeparatorCode(structure.getParentCode());
				if(parent != null) {
					departmentName = parent.getCodeName();
				}
			}
		}
		
		// separatorCode, 회사명(A001)가져오기
		Separator separator = separatorRepository.findById("A001").orElse(null);
		String companyName = separator.getSeparatorName();
		
		// Directory 엔티티 저장
		Directory directory = dto.toDirectoryEntity(
					employee,
					separator.getSeparatorCode(),
					email,
					departmentName,
					teamName,
					creatorName,
					companyName
				);

		
		employeeDirectoryRepository.save(directory);
		
	}
	
}
