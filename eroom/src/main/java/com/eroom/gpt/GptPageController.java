package com.eroom.gpt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GptPageController {

	

    @GetMapping("/gpt/test")
    public String goTestPage() {
    	return "gpt/gptTest";
    }
}
