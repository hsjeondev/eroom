package com.eroom.drive.dto;

import com.eroom.drive.entity.Drive;
import com.eroom.employee.entity.Employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DriverDto {

	private Long driveAttachNo; // 첨부파일 번호
    private Long uploaderNo; // 업로더 (사번 FK)
    private String uploaderName; // 업로더 이름
    private String separatorCode; // 공개범위 코드 (부서, 팀, 개인 등)
    private String driveOriName; // 원본파일명
    private String driveNewName; // 새파일명
    private String driveType; // 파일타입 (확장자)
    private Long driveSize; // 파일크기
    private String drivePath; // 파일경로
    private Long downloadCount; // 다운로드 횟수
    private String driveRegDate; // 등록일
    private String driveModDate; // 수정일
    private String driveEditor; //  수정자
    private String driveDeleteYn; // 삭제여부 
    
    // Entity -> DTO
	public static DriverDto toDto(Drive entity) {
		return DriverDto.builder()
				.driveAttachNo(entity.getDriveAttachNo())
				.uploaderNo(entity.getUploader().getEmployeeNo())
				.uploaderName(entity.getUploader().getEmployeeName())
				.separatorCode(entity.getSeparatorCode())
				.driveOriName(entity.getDriveOriName())
				.driveNewName(entity.getDriveNewName())
				.driveType(entity.getDriveType())
				.driveSize(entity.getDriveSize())
				.drivePath(entity.getDrivePath())
				.downloadCount(entity.getDownloadCount())
				.driveRegDate(entity.getDriveRegDate())
				.driveModDate(entity.getDriveModDate())
				.driveEditor(entity.getDriveEditor())
				.driveDeleteYn(entity.getDriveDeleteYn())
				.build();
	}
	// DTO -> Entity
	public Drive toEntity() {
		return Drive.builder()
				.uploader(Employee.builder().employeeNo(uploaderNo).build())
				.separatorCode(separatorCode)
				.driveOriName(driveOriName)
				.driveNewName(driveNewName)
				.driveType(driveType)
				.driveSize(driveSize)
				.drivePath(drivePath)
				.downloadCount(downloadCount)
				.driveEditor(driveEditor)
				.driveDeleteYn(driveDeleteYn)
				.build();
	}
    
}
