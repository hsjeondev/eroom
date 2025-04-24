package com.eroom.drive.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drive")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Drive {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="drive_attach_no")
	private Long driveAttachNo; // 첨부파일 번호
	
	@ManyToOne
	@JoinColumn(name="uploader")
	private Employee uploader; // 업로더 (사번 FK)
	
	@Column(name="separator_code")
	private String separatorCode; // 공개범위 코드 (부서, 팀, 개인 등)
	
	@Column(name="drive_ori_name")
	private String driveOriName; // 원본파일명
	
	@Column(name="drive_new_name")
	private String driveNewName; // 새파일명
	
	@Column(name="drive_type")
	private String driveType; // 파일타입 (확장자)
	
	@Column(name="drive_size")
	private Long driveSize; // 파일크기
	
	@Column(name="drive_path")
	private String drivePath; // 파일경로
	
	@Column(name="download_count")
	private Long downloadCount; // 다운로드 횟수
	
	@CreationTimestamp
	@Column(updatable=false, name="drive_reg_date")
	private LocalDateTime driveRegDate; // 등록일
	
	@UpdateTimestamp
	@Column(insertable=false, name="drive_mod_date")
	private LocalDateTime driveModDate; // 수정일
	
	@Column(name="drive_editor")
	private String driveEditor; // 수정자
	
	@Builder.Default
	@Column(name="drive_delete_yn")
	private String driveDeleteYn = "Y"; // 삭제여부
}
