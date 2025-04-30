package com.eroom.project.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EditTodoDetailRequest {
    private Long projectNo;
    private Long todoElementNo;
    private List<DetailDto> details;

    @Getter
    @Setter
    @ToString
    public static class DetailDto {
        private Long todoDetailNo;
        private String todoContent;
    }
}

