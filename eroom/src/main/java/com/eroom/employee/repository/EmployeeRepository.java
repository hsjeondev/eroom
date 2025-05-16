package com.eroom.employee.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eroom.employee.dto.SeparatorDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

//	Optional<Employee> findByEmployeeId(String employeeId);
	
	@Query("SELECT e FROM Employee e LEFT JOIN FETCH e.authorities WHERE e.employeeId = :employeeId")
	Optional<Employee> findByEmployeeId(@Param("employeeId") String employeeId);
	
	// 중복 제거 소속 목록
	@Query("SELECT DISTINCT s FROM Structure s WHERE s.visibleYn = 'Y'")
	List<Structure> findDistinctStructures();

	// 부서(소속) 이름(codeName) 기준으로 조회하는 메서드
	List<Employee> findByStructure_CodeName(String codeName);
	
	// 부서(소속) 코드(separatorCode) 기준으로 조회
	List<Employee> findByStructure_SeparatorCode(String separatorCode);
	
	// 특정 소속 (부모 코드)에 속한 직원 조회 | :parentCode(파라미터 값으로 가져오는것)
	@Query("SELECT e FROM Employee e WHERE e.structure.parentCode = :parentCode")
	List<Employee> findByStructureParentCode(@Param("parentCode") String parentCode);
	
	// 팀 번호로 소속 팀원 조회
	@Query("SELECT e FROM Employee e WHERE e.structure.structureNo = :teamId")
	List<Employee> findEmployeesByTeamId(@Param("teamId") String teamId);
	
	//Vehicle Reservation에서 예약자 이름을 가져옴
	Employee findByEmployeeNo(Long long1);
	
	// structureNo로 직원 조회
	List<Employee> findByStructure_StructureNo(Long structureNo);
	
	// 여러 팀 리스트로 직원 조회
	List<Employee> findByStructure_StructureNoIn(List<Long> structureNos);
	
	// 이름 중복 체크
	boolean existsByEmployeeName(String employeeName);
	
	// 사번 최대값 가져오기
	@Query("SELECT MAX(e.employeeNo) FROM Employee e")
	Long findMaxEmployeeNo();

	// 재직자 전부(퇴사자x)
	List<Employee> findAllByEmployeeEmploymentYn(String EmployeeEmploymentYn);
	
	

}
