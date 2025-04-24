package com.eroom.survey.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.security.EmployeeDetails;
import com.eroom.survey.dto.VoteRequest;
import com.eroom.survey.dto.VoteResultDto;
import com.eroom.survey.entity.SurveyItem;
import com.eroom.survey.entity.SurveyVote;
import com.eroom.survey.repository.SurveyItemRepository;
import com.eroom.survey.repository.SurveyRepository;
import com.eroom.survey.repository.SurveyVoteRepository;
import com.eroom.survey.repository.SurveyVoterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyVoteService {
	private final SurveyRepository surveyRepository;
	private final SurveyItemRepository surveyItemRepository;
    private final SurveyVoteRepository surveyVoteRepository;
    private final SurveyVoterRepository surveyVoterRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public boolean saveVote(VoteRequest request) {
        // 로그인한 사용자 정보 가져오기
        EmployeeDetails userDetails = (EmployeeDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long voterNo = userDetails.getEmployee().getEmployeeNo();

        boolean hasAuthority = surveyVoterRepository.existsBySurveyNoAndVoter(request.getSurveyId(), voterNo);
        if (!hasAuthority) {
            return false;
        }
        
        // 기존 투표 삭제
        surveyVoteRepository.deleteBySurveyNoAndVoter(request.getSurveyId(), voterNo);

        // 새 투표 저장
        List<SurveyVote> votes = new ArrayList<>();
        for (Long itemNo : request.getVotedItems()) {
            SurveyVote vote = SurveyVote.builder()
                    .surveyNo(request.getSurveyId())
                    .itemNo(itemNo)
                    .voter(voterNo)
                    .build();
            votes.add(vote);
        }

        surveyVoteRepository.saveAll(votes);
        return true;
    }


    public List<VoteResultDto> findVoteResults(Long surveyId) {
        List<SurveyItem> items = surveyItemRepository.findItemsBySurveyNo(surveyId);
        List<VoteResultDto> result = new ArrayList<>();

        // 현재 로그인한 사용자 이름
        String userName = ((EmployeeDetails) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal())
            .getEmployee()
            .getEmployeeName();

        for (SurveyItem item : items) {
            Long itemNo = item.getItemNo();

            // 해당 항목에 투표한 사람 목록 조회
            List<SurveyVote> votes = surveyVoteRepository.findByItemNo(itemNo);

            List<String> voterNames = votes.stream()
                .map(vote -> employeeRepository.findById(vote.getVoter())
                    .map(Employee::getEmployeeName)
                    .orElse(null))
                .collect(Collectors.toList());

            // 로그인 유저가 투표했는지 확인
            String voted = voterNames.contains(userName) ? "Y" : "N";

            // VoteResultDto 생성 및 추가
            result.add(VoteResultDto.of(item, votes.size(), voterNames, voted));
        }

        return result;
    }
}
