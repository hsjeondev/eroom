package com.eroom.survey.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "survey_item")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_no")
	private Long itemNo;

	@Column(name = "survey_no")
	private Long surveyNo;

	@Column(name = "item")
	private String item;
}
