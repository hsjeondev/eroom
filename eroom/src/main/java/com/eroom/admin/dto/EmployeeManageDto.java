package com.eroom.admin.dto;

import java.util.List;

import com.eroom.attendance.dto.AnnualLeaveDto;
import com.eroom.attendance.dto.AttendanceDto;
import com.eroom.attendance.entity.AnnualLeave;
import com.eroom.directory.dto.DirectoryDto;
import com.eroom.directory.entity.Directory;
import com.eroom.employee.dto.EmployeeDto;
import com.eroom.employee.dto.StructureDto;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Structure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EmployeeManageDto {
	
	// 사원 정보
    private EmployeeDto employee;
    
    // 주소록 정보
    private DirectoryDto directory;
    
    // 부서,팀  정보
    private StructureDto structure;
    
    // 근태 정보
    private List<AttendanceDto> attendanceList;
    
    // 연차 정보
    private AnnualLeaveDto annualLeave;
    
    private String department_name; // 부서명
    private String team_name; // 팀명
    private String formatted_hire_date; // 입사일
    private String formatted_end_date; // 퇴사일
    
    
    // toEntity 메소드 (DTO 안에 있는 정보를 각 Entity로 변환)
    // 수정, 저장시 DTO->Entity 변환에 사용
    // 개별 repository에 save()호출시 활용가능
    
    //  Employee Entity 변환
	public Employee toEmployeeEntity() {
		return employee.toEntity();
	}
	
	// Directory Entity 변환
	public Directory toDirectoryEntity() {
		return directory.toEntity();
	}
	
	// Structure Entity 변환
	public Structure toStructureEntity() {
		return structure.toEntity();
	}
	
	// AnnualLeave Entity 변환
	public AnnualLeave toAnnualLeaveEntity() {
		return annualLeave.toEntity();
	}
	
	// Attendance는 리스트 -> 각 AttendanceDto에서 toEntity()호출
    
    
    
    
    
    // Entity -> DTO 변환
    // 여러 Entity를 받아 통합DTO로 변환
    public static EmployeeManageDto toDto(EmployeeDto employeeDto, DirectoryDto directoryDto, StructureDto structureDto,
    		List<AttendanceDto> attendanceList, AnnualLeaveDto annualLeaveDto,
    		String departmentName, String teamName, String formattedHireDate, String formattedEndDate) {
    		return EmployeeManageDto.builder() 
					.employee(employeeDto)
					.directory(directoryDto)
					.structure(structureDto)
					.attendanceList(attendanceList)
					.annualLeave(annualLeaveDto)
					.department_name(departmentName)
					.team_name(teamName)
					.formatted_hire_date(employeeDto.getEmployee_hire_date() != null ? 
							employeeDto.getEmployee_hire_date().toLocalDate().toString() : "-")
					.formatted_end_date(employeeDto.getEmployee_end_date() != null ? 
							employeeDto.getEmployee_end_date().toLocalDate().toString() : "-")
					.build();
    }
    
    
}
