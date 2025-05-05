package com.eroom.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_meeting_minute")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMeetingMinute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_minute_no")
    private Long meetingMinuteNo;

    @Column(name = "project_no")
    private Long projectNo;

    @Column(name = "meeting_minute_writer")
    private String meetingMinuteWriter;

    @Column(name = "meeting_title")
    private String meetingTitle;

    @Column(name = "meeting_content")
    private String meetingContent;

    @Column(name = "meeting_date")
    private LocalDateTime meetingDate;
}
