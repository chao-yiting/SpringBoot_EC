package com.example.demo.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.User;
import com.example.demo.request.UserRequest;
import com.example.demo.service.admin.AdminAuthUserService;
import com.example.demo.service.admin.AdminUsersService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AdminUsersController {

	@Autowired
	private AdminUsersService adminUsersService;

	@Autowired
	private AdminAuthUserService adminAuthUserService;

	@GetMapping("/admin/users")
	public String orderList(HttpSession session, Model model, UserRequest userRequest) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}

		// すべてのユーザーを取得
		List<User> users = adminUsersService.getAllUsers();

		model.addAttribute("users", users);
		model.addAttribute("userAuthority", new UserRequest());

		return "admin/admin_users";
	}

	//ユーザー権限変更
	@PostMapping("/updateUserAuthority/{userId}")
	public String updateUserAuthority(@Valid @ModelAttribute("userRequest") UserRequest userRequest,
			BindingResult result, HttpSession session, Model model,
			@PathVariable Long userId) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}
		// デバッグログ
		System.out.println("UserRequest: " + userRequest.getUserAuthority());

		//バリデーション
		if (result.hasErrors()) {
			// デバッグ情報をログに出力
			System.out.println("Validation errors: " + result.getAllErrors());
			// ビュー内で表示
			model.addAttribute("users", adminUsersService.getAllUsers());

			// 商品カテゴリーページのテンプレートを返す
			return "admin/admin_users";
		}

		// ユーザー権限を変更するロジックを実装
		adminUsersService.updateUserAuthority(userId, userRequest);
		return "redirect:/admin/users";
	}

	//ユーザー削除
	@PostMapping("/deleteUser/{userId}")
	public String deleteUser(@PathVariable Long userId, HttpSession session) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}
		// ユーザーをデータベースから削除するロジックを実装
		adminUsersService.deleteUser(userId);
		return "redirect:/admin/users"; // 削除後、ユーザー管理ページにリダイレクト
	}
}
