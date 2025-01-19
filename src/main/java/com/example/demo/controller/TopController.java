package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class TopController {

	@GetMapping("/")
    public String move_top(HttpSession session) {
        return "redirect:/products";
    }
	
}
