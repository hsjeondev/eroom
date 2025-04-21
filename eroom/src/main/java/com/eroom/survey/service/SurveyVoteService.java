package com.eroom.survey.service;

import org.springframework.stereotype.Service;

import com.eroom.survey.dto.SurveyVoteDto;
import com.eroom.survey.entity.SurveyVote;
import com.eroom.survey.repository.SurveyVoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyVoteService {

    private final SurveyVoteRepository surveyVoteRepository;

    public void saveVote(SurveyVoteDto dto) {
        // 중복 여부 확인
        boolean exists = surveyVoteRepository.existsBySurveyNoAndItemNoAndVoter(
                dto.getSurveyNo(),
                dto.getItemNo(),
                dto.getVoter()
        );

        if (exists) {
            // 중복일 경우 저장하지 않음
            System.out.println("중복 투표 방지됨: 설문 " + dto.getSurveyNo() + ", 항목 " + dto.getItemNo() + ", 유저 " + dto.getVoter());
            return;
        }

        // 새 투표 저장
        SurveyVote vote = dto.toEntity();
        surveyVoteRepository.save(vote);
    }
}
