package com.eroom.survey.dto;

import com.eroom.survey.entity.SurveyVote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyVoteDto {

    private Long surveyNo;
    private Long itemNo;
    private Long voter;

    // DTO → Entity 변환
    public SurveyVote toEntity() {
        return SurveyVote.builder()
                .surveyNo(this.surveyNo)
                .itemNo(this.itemNo)
                .voter(this.voter)
                .build();
    }

    // Entity → DTO 변환
    public static SurveyVoteDto toDto(SurveyVote entity) {
        return SurveyVoteDto.builder()
                .surveyNo(entity.getSurveyNo())
                .itemNo(entity.getItemNo())
                .voter(entity.getVoter())
                .build();
    }
}
