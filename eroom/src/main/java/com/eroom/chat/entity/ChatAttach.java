package com.eroom.chat.entity;

import java.time.LocalDateTime;

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
import lombok.Setter;

@Entity
@Table(name="chat_attach")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatAttach {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="chat_attach_no")
	private Long chatAttachNo; // 파일번호
	
	@Column(name="chat_attach_ori_name")
	private String chatAttachOriName; // 기존파일명
	 
	@Column(name="chat_attach_new_name")
	private String chatAttachNewName; // 변경파일명
	
	@Column(name="chat_attach_path")
	private String chatAttachPath; // 파일경로
}
