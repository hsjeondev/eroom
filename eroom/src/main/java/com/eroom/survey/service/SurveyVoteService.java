package com.eroom.survey.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eroom.employee.entity.Employee;
import com.eroom.employee.repository.EmployeeRepository;
import com.eroom.security.EmployeeDetails;
import com.eroom.survey.dto.SurveyVoteDto;
import com.eroom.survey.dto.VoteResultDto;
import com.eroom.survey.entity.SurveyItem;
import com.eroom.survey.entity.SurveyVote;
import com.eroom.survey.repository.SurveyItemRepository;
import com.eroom.survey.repository.SurveyVoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyVoteService {
	private final SurveyItemRepository surveyItemRepository;
    private final SurveyVoteRepository surveyVoteRepository;
    private final EmployeeRepository employeeRepository;

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

            // ✅ 해당 항목에 투표한 사람 목록 조회
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
