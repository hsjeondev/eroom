package com.eroom.project.entity;

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
@Table(name = "project_meeting_minutes_mapping")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMeetingMinuteMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_mapping_no")
    private Long meetingMappingNo;

    @Column(name = "meeting_minute_no")
    private Long meetingMinuteNo;

    @Column(name = "employee_no")
    private Long employeeNo;
}
