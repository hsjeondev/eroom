package com.eroom.article.dto;

import java.time.LocalDateTime;


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
	
	private LocalDateTime article_reg_date;
	private LocalDateTime article_mod_date;

	
	private Long article_creator;
	private Long article_editor;
	private String separator_code;
	private Integer order;








    
    
}
