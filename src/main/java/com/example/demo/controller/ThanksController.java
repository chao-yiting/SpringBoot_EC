package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThanksController {
	
	@GetMapping("/thanks")
    public String move_thanks() {
        return "thanks";
    }

}
