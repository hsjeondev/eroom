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
@AllArgsConstructor
public class GptResponseDto {
	 private List<Choice> choices;

	    public static class Choice {
	        private Message message;

	        public Message getMessage() {
	            return message;
	        }
	    }

	    public static class Message {
	        private String role;
	        private String content;

	        public String getContent() {
	            return content;
	        }
	    }

	    public List<Choice> getChoices() {
	        return choices;
	    }
}
