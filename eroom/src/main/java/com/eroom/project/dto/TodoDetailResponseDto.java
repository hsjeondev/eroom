package com.eroom.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoDetailResponseDto {

    private Long todoNo;                 // 할 일 번호
    private String todoTitle;             // 할 일 제목
    private String emergency;             // 긴급 여부
    private String listName;              // 리스트 이름
    private Long listNo;                  // 리스트 번호
    private String listColor;             // 리스트 색상
    private String employeeName;          // 담당자 이름
    private Long employeeNo;              // 담당자 번호
    private List<ProjectTodoElementDetailDto> details; // 세부 할 일 목록
}

