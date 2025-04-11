package com.eroom.survey.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.eroom.survey.entity.SurveyItem;

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
public class SurveyItemDto {
	private Long itemNo;
	private Long surveyNo;
	private List<String> items;
	
	public List<SurveyItem> toEntityList() {
		return items.stream()
				.map(itemStr -> SurveyItem.builder()
						.surveyNo(surveyNo)
						.item(itemStr)
						.build())
				.collect(Collectors.toList());
	}
	
	public static SurveyItemDto toDto(SurveyItem entity) {
		return SurveyItemDto.builder()
				.itemNo(entity.getItemNo())
				.surveyNo(entity.getSurveyNo())
				.items(List.of(entity.getItem()))
				.build();
	}
}
