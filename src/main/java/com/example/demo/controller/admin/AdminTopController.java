package com.example.demo.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.admin.AdminAuthUserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminTopController {

	@Autowired
	private AdminAuthUserService adminAuthUserService;

	@GetMapping("/admin/top")
	public String viewAdminTop(HttpSession session, Model model) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}
		return "admin/admin_top";
	}
}
