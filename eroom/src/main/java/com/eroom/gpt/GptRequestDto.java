package com.eroom.gpt;

import java.util.List;

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
public class GptRequestDto {
	private String model;
    private List<GptMessageDto> messages;

    public GptRequestDto(String model, List<GptMessageDto> messages) {
        this.model = model;
        this.messages = messages;
    }
}
