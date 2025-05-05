package com.eroom.project.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @CreationTimestamp
    @Column(name = "meeting_date")
    private LocalDateTime meetingDate;
    
    public void updateTitleAndContent(String title, String content) {
        this.meetingTitle = title;
        this.meetingContent = content;
    }
}
