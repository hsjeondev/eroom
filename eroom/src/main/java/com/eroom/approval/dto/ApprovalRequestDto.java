package com.eroom.approval.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.eroom.employee.dto.EmployeeDto;

import lombok.Data;

@Data
public class ApprovalRequestDto {
    private EmployeeDto writer;
    private String title;
    private int format_no;
    private Map<String, String> content;
    private LocalDateTime approval_reg_date;
    
    private List<Long> approverIds;
    private List<Integer> approverSteps;

    private List<Long> agreerIds;
    private List<Integer> agreerSteps;

    private List<Long> refererIds;
    private List<Integer> refererSteps;
    
    private Long editApprovalNo;

}
