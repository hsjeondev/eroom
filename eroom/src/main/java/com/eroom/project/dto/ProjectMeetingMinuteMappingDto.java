package com.eroom.project.dto;

import java.util.ArrayList;
import java.util.List;

import com.eroom.project.entity.ProjectMeetingMinuteMapping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProjectMeetingMinuteMappingDto {

    private Long meetingMinuteNo;
    private List<Long> participants;

    public List<ProjectMeetingMinuteMapping> toEntityList() {
        List<ProjectMeetingMinuteMapping> result = new ArrayList<>();
        for (Long empNo : participants) {
            result.add(ProjectMeetingMinuteMapping.builder()
                .meetingMinuteNo(meetingMinuteNo)
                .employeeNo(empNo)
                .build());
        }
        return result;
    }
}
