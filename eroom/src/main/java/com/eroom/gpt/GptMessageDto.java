package com.eroom.gpt;

import java.time.LocalDateTime;
import java.util.List;

import com.eroom.directory.entity.Directory;
import com.eroom.directory.entity.DirectoryMemo;
import com.eroom.employee.entity.Employee;
import com.eroom.employee.entity.Separator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class GptMessageDto {
	private String role;
    private String content;
    
    public GptMessageDto(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
