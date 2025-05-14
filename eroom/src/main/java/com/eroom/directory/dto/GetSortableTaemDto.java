package com.eroom.directory.dto;

import java.util.List;

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
public class GetSortableTaemDto {
    private String teamId;    // 팀 ID
    private String teamName;  // 팀명
    private int order;        // 팀 순서
}
