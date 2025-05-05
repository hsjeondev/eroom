package com.eroom.project.dto;

import java.time.LocalDateTime;

import com.eroom.project.entity.ProjectMeetingMinute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMeetingMinuteDto {
    private Long meetingMinuteNo;
    private String meetingTitle;
    private int participants;
    private String writer;
    private LocalDateTime meetingDate;
    
    public static ProjectMeetingMinuteDto toEntity(ProjectMeetingMinute entity) {
        return ProjectMeetingMinuteDto.builder()
                .meetingMinuteNo(entity.getMeetingMinuteNo())
                .meetingTitle(entity.getMeetingTitle())
                .participants(0)
                .writer(entity.getMeetingMinuteWriter())
                .meetingDate(entity.getMeetingDate())
                .build();
    }
}