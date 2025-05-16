package com.eroom.article.dto;

import java.time.LocalDateTime;

import com.eroom.article.entity.Article;
import com.eroom.employee.entity.Employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ArticleDto {
	private Long article_no;
	private Long employee_no;
	private String article_title;
	private String article_content;
	
	@Builder.Default
	private String visible_yn="Y";
	
	@Builder.Default
	private String article_emergency_yn="N";
	
	
	private LocalDateTime article_reg_date;
	private LocalDateTime article_mod_date;

	
	private Long article_creator;
	private Long article_editor;
	@Builder.Default
	private String separator_code ="B001";
	private Long order_num;



	public Article toEntity() {
	    return Article.builder()
	            .articleNo(article_no)
	            .employee(Employee.builder().employeeNo(employee_no).build()) // 작성자 FK
	            .articleTitle(article_title)
	            .articleContent(article_content)
	            .separatorCode(separator_code)
	            .order_num(order_num)
	            .visibleYn(visible_yn)
	            .articleEmergencyYn(article_emergency_yn)
	            .articleCreator(article_creator)
	            .articleEditor(article_editor)
	            .articleRegDate(article_reg_date)
	            .articleModDate(article_mod_date)
	            .build();
	}

	public ArticleDto(String articleTitle, LocalDateTime articleRegDate, Long articleNo) {
        this.article_title = articleTitle;
        this.article_reg_date = articleRegDate;
        this.article_no = articleNo;
    }


    
    
}
