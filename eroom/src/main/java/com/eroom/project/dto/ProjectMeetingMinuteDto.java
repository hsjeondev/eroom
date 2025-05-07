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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMeetingMinuteDto {

    private Long meetingMinuteNo;
    private Long projectNo;
    private String meetingTitle;
    private String meetingContent;
    private String writer;
    private LocalDateTime meetingDate;
    private String meetingMinuteVisible = "Y";
    private int participants;

    // Entity → DTO
    public static ProjectMeetingMinuteDto toDto(ProjectMeetingMinute entity) {
        return ProjectMeetingMinuteDto.builder()
                .meetingMinuteNo(entity.getMeetingMinuteNo())
                .projectNo(entity.getProjectNo())
                .meetingTitle(entity.getMeetingTitle())
                .meetingContent(entity.getMeetingContent())
                .writer(entity.getMeetingMinuteWriter())
                .meetingDate(entity.getMeetingDate())
                .meetingMinuteVisible(entity.getMeetingMinuteVisible())
                .build();
    }

    // DTO → Entity
    public ProjectMeetingMinute toEntity() {
        return ProjectMeetingMinute.builder()
                .meetingMinuteNo(meetingMinuteNo)
                .projectNo(projectNo)
                .meetingTitle(meetingTitle)
                .meetingContent(meetingContent)
                .meetingMinuteWriter(writer)
                .meetingDate(meetingDate)
                .meetingMinuteVisible(meetingMinuteVisible != null ? meetingMinuteVisible : "Y")
                .build();
    }
}
