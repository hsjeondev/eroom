package com.eroom.chat.entity;

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
@Table(name="chat_attach_mapping")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatAttachMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="chat_attach_mapping_no")
	// 파일매핑번호
	private Long chatAttachMappingNo;
	
	// 첨부파일번호 FK
	
	// 메시지번호 FK
	
	
}
