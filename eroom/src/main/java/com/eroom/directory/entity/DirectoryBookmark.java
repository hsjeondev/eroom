package com.eroom.directory.entity;

import java.time.LocalDateTime;

import com.eroom.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@ToString
@Builder

@Entity
@Table(name="directory_bookmark")
public class DirectoryBookmark {
	
	@Id
	@Column(name="directory_bookmark_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long directory_bookmark_no; // 직원 북마크 번호
	
	@Column(name="directory_bookmark_yn")
	private String directoryBookmarkYn; // 북마크 여부
	@Column(name="directory_bookmark_creator", updatable = false)
	private String directoryBookmarkCreator; // 북마크 생성자
	@Column(name="directory_bookmark_editor", insertable = false)
	private String directoryBookmarkEditor; // 수정자
	@Column(name="visible_yn", insertable = false)
	private String visibleYn; // 사용여부
	@Column(name = "directory_bookmark_reg_date", insertable = false, updatable = false)
	private LocalDateTime directoryBookmarkRegDate; // 생성시간
	@Column(name = "directory_bookmark_mod_date", insertable = false, updatable = false)
	private LocalDateTime directoryBookmarkModDate; // 수정시간
	
	@ManyToOne
	@JoinColumn(name = "directory_no")
	private Directory directory;
	@ManyToOne
	@JoinColumn(name = "employee_no")
	private Employee employee;
	
	
}
