package com.eroom.directory.dto;

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
public class TeamSortDto {
    private String teamSeparatorCode;
    private String teamCodeName;
    private Long teamSortOrder;
    private String newDepartmentSeparatorCode;

}
