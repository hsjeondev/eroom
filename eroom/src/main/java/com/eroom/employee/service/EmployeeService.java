package com.eroom.employee.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.attendance.repository.AnnualLeaveRepository;
import com.eroom.directory.entity.Directory;
import com.eroom.directory.repository.DirectoryRepository;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.EmployeeUpdateDto;
import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.dto.StructureDto;
import com.eroom.employee.entity.Authority;
import com.eroom.employee.entity.AuthorityMapping;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Separator;
import com.eroom.employee.entity.Structure;
import com.eroom.employee.repository.AuthorityMappingRepository;
import com.eroom.employee.repository.AuthorityRepository;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.employee.repository.SeparatorRepository;
import com.eroom.employee.repository.StructureRepository;
import com.eroom.security.EmployeeDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
	private final EmployeeRepository employeeRepository;
	private final StructureRepository structureRepository;
	private final DirectoryRepository employeeDirectoryRepository;
	private final SeparatorRepository separatorRepository;
	private final AuthorityRepository authorityRepository;
	private final AuthorityMappingRepository authorityMappingRepository;
	private final PasswordEncoder passwordEncoder;
	private final AnnualLeaveRepository annualLeaveRepository;
	
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
		return structureRepository.findVisibleBySeparatorCodeStartingWith("T")
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
		String creatorName = employeeDetail.getEmployee().getEmployeeId(); // 주소록 생성한 아이디
		
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
		
		int currentYear = LocalDate.now().getYear();
		AnnualLeave annualLeave = AnnualLeave.builder()
								.employee(employee)
								.year((long)currentYear)
								.annualLeaveTotal(0.0)
								.annualLeaveUsed(0.0)
								.build();
		annualLeaveRepository.save(annualLeave);
	    // 권한 엔티티 조회(ROLE_USER)
		Authority userAuthority = authorityRepository.findById(1L).orElse(null);
		// 권한 매핑 생성 및 저장
		AuthorityMapping authorityMapping = AuthorityMapping.builder()
										.employee(employee)
										.authority(userAuthority)
										.build();
		authorityMappingRepository.save(authorityMapping);
	}
	
	// 회원 정보 수정(관리자)
	@Transactional
	public void updateEmployee(EmployeeUpdateDto dto) {
		// employee 가져오기
		Employee employee = employeeRepository.findById(dto.getEmployee_no()).orElse(null);

		// employee 수정 전 정보 백업
		String originalEmployeeName = employee.getEmployeeName();
		// 이름 수정 
		if(dto.getEmployee_name() != null && !dto.getEmployee_name().equals(employee.getEmployeeName())) employee.setEmployeeName(dto.getEmployee_name());
		// 직급 수정
		if(dto.getEmployee_position() != null && !dto.getEmployee_position().equals(employee.getEmployeePosition())) employee.setEmployeePosition(dto.getEmployee_position());
		// 부서 / 팀 변경
		if(dto.getStructure_no() != null) {
			Long newStructureNo = dto.getStructure_no();
			Long currentStructureNo = (employee.getStructure() != null) ? employee.getStructure().getStructureNo() : null;
			
			if(!newStructureNo.equals(currentStructureNo)) {
				Structure structure = structureRepository.findById(newStructureNo).orElse(null);
				employee.setStructure(structure);
			}
		}
		// 생년월일 수정 -> 비밀번호도 재암호화 해서 수정 필요함
		if(dto.getEmployee_birth() != null && !dto.getEmployee_birth().equals(employee.getEmployeeBirth())) {
			employee.setEmployeeBirth(dto.getEmployee_birth()); // 생일 문자열 그대로 저장
			// 생년월일이 수정된 경우에 비밀번호 다시 세팅
			String birthNoDash = dto.getEmployee_birth().replaceAll("-", ""); // 하이픈 제거 
			String newEncodedPw = passwordEncoder.encode(birthNoDash);
			employee.setEmployeePw(newEncodedPw); // 암호화된 비밀번호로 재설정해줌
		}
		// 입사일 수정
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		//if(dto.getEmployee_hire_date() != null && !dto.getEmployee_hire_date().equals(employee.getEmployeeHireDate())) employee.setEmployeeHireDate(dto.getEmployee_hire_date());
		if(dto.getEmployee_hire_date() != null && !dto.getEmployee_hire_date().isEmpty()) {
			LocalDate hireDate = LocalDate.parse(dto.getEmployee_hire_date(),formatter);
			if(!hireDate.atStartOfDay().equals(employee.getEmployeeHireDate())) {
				employee.setEmployeeHireDate(hireDate.atStartOfDay());
			}
		}
		// 퇴사일 수정
		if(dto.getEmployee_end_date() == null || dto.getEmployee_end_date().isEmpty()) {
			employee.setEmployeeEndDate(null);
		}else {
			LocalDate endDate = LocalDate.parse(dto.getEmployee_end_date(),formatter);
			if(!endDate.atStartOfDay().equals(employee.getEmployeeEndDate())) {
				employee.setEmployeeEndDate(endDate.atStartOfDay());
			}
		}
		// 재직여부 수정
		if(dto.getEmployee_employment_yn() != null) employee.setEmployeeEmploymentYn(dto.getEmployee_employment_yn());
		
		employeeRepository.save(employee);
		
		
		// directory 수정
		Directory directory = employee.getDirectory();
		if(directory != null) {
			boolean updated = false;
			// 이름이 바뀐 경우 주소록에도 이름 변경
			if(dto.getEmployee_name() != null && !dto.getEmployee_name().equals(originalEmployeeName)) {
				directory.setDirectoryName(dto.getEmployee_name());
				updated = true;
			}
			// 연락처가 바뀐 경우
			if(dto.getDirectory_phone() != null && !dto.getDirectory_phone().equals(directory.getDirectoryPhone())) {
				directory.setDirectoryPhone(dto.getDirectory_phone());
				updated = true;
			}
			
			// 우편번호 변경 감지
			if(dto.getDirectory_zipcode() != null && !dto.getDirectory_zipcode().equals(directory.getDirectoryZipcode())) {
				directory.setDirectoryZipcode(dto.getDirectory_zipcode());
				updated = true;
			}
			// 주소 변경 감지
			if(dto.getDirectory_address() != null && !dto.getDirectory_address().equals(directory.getDirectoryAddress())) {
				directory.setDirectoryAddress(dto.getDirectory_address());
				updated = true;
			}
			
			// visible_yn 퇴사자 : N / 재직자 : Y
			String employmentYn = dto.getEmployee_employment_yn();
			if(employmentYn != null) {
				String visibleYn = employmentYn.equals("Y") ? "Y" : "N";
				if(!visibleYn.equals(directory.getVisibleYn())) {
					directory.setVisibleYn(visibleYn);
					updated = true;
				}
			}
			
			// 부서 / 팀
			Structure newStructure = employee.getStructure();
			String newDepartmentName = "-";
			String newTeamName = "-";
			
			if(newStructure != null) {
				if(newStructure.getParentCode() == null) {
					newDepartmentName = newStructure.getCodeName();
				}else {
					newTeamName = newStructure.getCodeName();
					Structure parent = structureRepository.findBySeparatorCode(newStructure.getParentCode());
					if(parent != null) {
						newDepartmentName = parent.getCodeName();
					}
				}
			}
			
			if(!newDepartmentName.equals(directory.getDirectoryDepartment())) {
				directory.setDirectoryDepartment(newDepartmentName);
				updated = true;
			}
			if(!newTeamName.equals(directory.getDirectoryTeam())) {
				directory.setDirectoryTeam(newTeamName);
				updated = true;
			}
			
			// 직급 변경
			if(dto.getEmployee_position() != null && !dto.getEmployee_position().equals(directory.getDirectoryPosition())) {
				directory.setDirectoryPosition(dto.getEmployee_position());
				updated = true;
			}
			
			// 이름 변경
//			if(dto.getEmployee_name() != null && !dto.getEmployee_name().equals(directory.getDirectoryName())) {
//				directory.setDirectoryName(dto.getEmployee_name());
//				updated = true;
//			}
			
			if(updated) {
				// 수정일자 갱신
				directory.setDirectoryModDate(LocalDateTime.now());
				
				// 현재 로그인 사용자 정보를 editor로 저장
				EmployeeDetails employeeDetail = (EmployeeDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				String editorId = employeeDetail.getEmployee().getEmployeeId();
				directory.setDirectoryEditor(editorId);

				employeeDirectoryRepository.save(directory);
			}
			
		}
	}
	
	// 회원 정보 수정 (마이페이지)
	@Transactional
	public void updateEmployeeFromMypage(EmployeeUpdateDto dto) {
	    // 로그인한 사용자 본인 정보만 가져옴
	    EmployeeDetails employeeDetail = (EmployeeDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    Long employeeNo = employeeDetail.getEmployee().getEmployeeNo();
	    Employee employee = employeeRepository.findById(employeeNo).orElseThrow(() -> new RuntimeException("회원 없음"));

	    // 이름만 바뀐 경우 employee에도 반영
	    if (dto.getEmployee_name() != null && !dto.getEmployee_name().equals(employee.getEmployeeName())) {
	        employee.setEmployeeName(dto.getEmployee_name());
	    }

	    // Directory 정보 수정
	    Directory directory = employee.getDirectory();
	    boolean updated = false;

	    if (directory != null) {
	        if (dto.getDirectory_phone() != null && !dto.getDirectory_phone().equals(directory.getDirectoryPhone())) {
	            directory.setDirectoryPhone(dto.getDirectory_phone());
	            updated = true;
	        }
	        if (dto.getDirectory_zipcode() != null && !dto.getDirectory_zipcode().equals(directory.getDirectoryZipcode())) {
	            directory.setDirectoryZipcode(dto.getDirectory_zipcode());
	            updated = true;
	        }
	        if (dto.getDirectory_address() != null && !dto.getDirectory_address().equals(directory.getDirectoryAddress())) {
	            directory.setDirectoryAddress(dto.getDirectory_address());
	            updated = true;
	        }
	        if (dto.getEmployee_name() != null && !dto.getEmployee_name().equals(directory.getDirectoryName())) {
	            directory.setDirectoryName(dto.getEmployee_name()); // 이름 동기화
	            updated = true;
	        }

	        if (updated) {
	            directory.setDirectoryModDate(LocalDateTime.now());
	            directory.setDirectoryEditor(employeeDetail.getEmployee().getEmployeeId());
	            employeeDirectoryRepository.save(directory);
	        }
	    }

	    employeeRepository.save(employee);
	}
	
	// 회원 정보 삭제 (퇴사 처리)
	@Transactional
	public void deleteEmployee(EmployeeUpdateDto dto) {
		Employee employee = employeeRepository.findById(dto.getEmployee_no()).orElse(null);
		
		// 퇴사 처리
		if(dto.getEmployee_end_date() != null && !dto.getEmployee_end_date().isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate endDate = LocalDate.parse(dto.getEmployee_end_date(),formatter);
			employee.setEmployeeEndDate(endDate.atStartOfDay());
		}
		// 재직 여부 N 설정
		employee.setEmployeeEmploymentYn("N");
		employeeRepository.save(employee);
		
		Directory directory = employee.getDirectory();
		if(directory != null) {
			directory.setVisibleYn("N"); // 주소록 숨김 처리
			directory.setDirectoryModDate(LocalDateTime.now()); // 수정 날짜 
			
			// directory_editor
			EmployeeDetails employeeDetail = (EmployeeDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String editorId = employeeDetail.getEmployee().getEmployeeId();
			directory.setDirectoryEditor(editorId);
			
			employeeDirectoryRepository.save(directory);
			
		}
	}
	
	// 아이디로 Employee 조회
	public Employee findEmployeeByEmployeeId(String employeeId) {
		return employeeRepository.findByEmployeeId(employeeId).orElse(null);
	}
	
	
	// 전체 직원 조회
	public List<Employee> findAllEmployee_EmployeeEmploymentYn() {
		return employeeRepository.findAllByEmployeeEmploymentYn("Y");
	}
	
}