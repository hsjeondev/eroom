package com.eroom.drive.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
public class PersonalDriveSummaryDto {

   private Long totalFiles;      // 전체 파일 수
   private Long totalDownloads; // 총 다운로드 수
   private String recentUploadDate; // 최근 업로드 날짜 (yyyy-MM-dd)
	
}
