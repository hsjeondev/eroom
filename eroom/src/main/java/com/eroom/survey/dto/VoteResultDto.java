package com.eroom.survey.dto;

import java.util.List;

import com.eroom.survey.entity.SurveyItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResultDto {
    private Long itemNo;
    private String name;
    private int count;
    private List<String> voters;
    private String voted;
    
    public static VoteResultDto of(SurveyItem item, int count, List<String> voters, String voted) {
        return VoteResultDto.builder()
                .itemNo(item.getItemNo())
                .name(item.getItem())
                .count(count)
                .voters(voters)
                .voted(voted)
                .build();
    }
}
