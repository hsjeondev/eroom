package com.eroom.article.entity;

import java.time.LocalDateTime;

import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="article")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {

	@Id
    @Column(name = "article_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleNo;

    
	@ManyToOne
	@JoinColumn(name="employee_no")
	private Employee employee;

    @Column(name = "separator_code")
    private String separatorCode; // 기본값 설정

    @Column(name = "article_title")
    private String articleTitle;

    @Column(name = "article_content")
    private String articleContent;

    @Column(name = "order_num")
    private Long order_num;  //

    @Column(name = "visible_yn")
    private String visibleYn ; // 기본값

    @Column(name = "article_creator")
    private Long articleCreator;

    @Column(name = "article_editor")
    private Long articleEditor;

    @Column(name = "article_reg_date")
    private LocalDateTime articleRegDate;

    @Column(name = "article_mod_date")
    private LocalDateTime articleModDate;
}
