package com.eroom.survey.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.eroom.survey.dto.SurveyItemDto;
import com.eroom.survey.entity.SurveyItem;
import com.eroom.survey.repository.SurveyItemRepository;
import com.eroom.survey.repository.SurveyVoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyItemService {

	private final SurveyItemRepository surveyItemRepository;
	private final SurveyVoteRepository surveyVoteRepository;

	public List<SurveyItemDto> findItemsBySurveyNo(Long surveyNo) {
		return surveyItemRepository.findItemsBySurveyNo(surveyNo)
				.stream()
				.map(SurveyItemDto::toDto)
				.collect(Collectors.toList());
	}

	public List<SurveyItemDto> findVotedItem(Long surveyNo, Long voterId) {
		List<SurveyItem> items = surveyItemRepository.findItemsBySurveyNo(surveyNo);
		List<Long> votedItem = surveyVoteRepository.findItemBySurveyNoAndVoter(surveyNo, voterId);

		return items.stream()
				.map(item -> {
					int count = surveyVoteRepository.countByItemNo(item.getItemNo());
					boolean isVoted = votedItem.contains(item.getItemNo());
					return SurveyItemDto.toDto(item, count, isVoted);
				})
				.collect(Collectors.toList());
	}
}
