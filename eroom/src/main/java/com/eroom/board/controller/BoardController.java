package com.eroom.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardController {

	@GetMapping("/board/notice")
	public String selectBoardNotice() {
		return "/board/boardNotice";
	}
	
	@GetMapping("/board/anonymous")
	public String selectBoardAnonymousAll() {
		return "/board/boardAnonymous";
	}
	
}
