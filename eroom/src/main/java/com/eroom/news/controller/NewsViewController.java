package com.eroom.news.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NewsViewController {

	 @GetMapping("/news")
	    public String newsPage() {
	        return "news/news";
	    }
}
