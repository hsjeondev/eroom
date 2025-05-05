package com.eroom.directory.dto;

import java.util.List;
import java.util.Map;

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
public class UpdateSortOrderDto {
    private List<DepartmentSortDto> sortData;
}
