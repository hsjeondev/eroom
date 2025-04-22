// 투표 요청용 dto
// 투표 저장과 분리하기위함으로 데이터베이스와 관계없음.
package com.eroom.survey.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRequest {
    private Long surveyId;
    private List<Long> votedItems;
}
