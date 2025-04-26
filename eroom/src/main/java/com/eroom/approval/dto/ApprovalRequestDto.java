package com.eroom.approval.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.eroom.drive.dto.DriveDto;
import com.eroom.employee.dto.EmployeeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
public class ApprovalRequestDto {
    private EmployeeDto writer;
    private String title;
    private Long format_no;
    private Map<String, String> content;
    private LocalDateTime approval_reg_date;
    
    private List<Long> approverIds;
    private List<Integer> approverSteps;

    private List<Long> agreerIds;
    private List<Integer> agreerSteps;

    private List<Long> refererIds;
    private List<Integer> refererSteps;
    
    private Long editApprovalNo;
    
    private List<MultipartFile> files;

}
