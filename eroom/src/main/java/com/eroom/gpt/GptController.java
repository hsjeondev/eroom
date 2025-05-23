package com.eroom.gpt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/gpt")
@RequiredArgsConstructor
public class GptController {
    private final GptService gptService;

    @GetMapping("/ask")
    public String askGpt(@RequestParam("prompt") String prompt) {
        return gptService.askGpt(prompt);
    }
    
}
