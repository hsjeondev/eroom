package com.eroom.calendar.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eroom.employee.entity.Employee;

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

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="calendar")
@Builder
@Getter
public class CompanyCalendar {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_no")
    private Long calendarNo;

    @Column(name = "calendar_start_time")
    private LocalDateTime calendarStartTime;

    @Column(name = "calendar_end_time")
    private LocalDateTime calendarEndTime;

    @Column(name = "calendar_title")
    private String companyTitle;

    @Column(name = "calendar_location")
    private String companyLocation;

    @Column(name = "calendar_content")
    private String companyContent;
      
    @CreationTimestamp
    @Column(updatable = false, name = "calendar_reg_date")
    private LocalDateTime calendarRegDate;

    @UpdateTimestamp
    @Column(insertable = false, name = "calendar_mod_date")
    private LocalDateTime calendarModDate;

    @Column(name = "employee_no") // FK 대신 단순 값으로 저장
    private Long employeeNo;

    @Column(name = "`separator`")
    private String separator;
    
    @Builder.Default
    @Column(name = "`visible_yn`")
    private String visibleYn = "Y";

    @Column(name = "calendar_creator")
    private String companyCreator;

    @Column(name = "calendar_editor")
    private String companyEditor;


	

}
