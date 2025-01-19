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

import com.example.demo.entity.Category;
import com.example.demo.request.CategoryRequest;
import com.example.demo.service.admin.AdminAuthUserService;
import com.example.demo.service.admin.AdminCategoriesService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AdminCategoriesController {

	@Autowired
	private AdminAuthUserService adminAuthUserService;

	@Autowired
	private AdminCategoriesService adminCategoriesService;

	@GetMapping("/admin/categories")
	public String CategoriesList(Model model, HttpSession session) { //Model: ビューにデータを渡す
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}

		// カテゴリーをServiceから取得
		List<Category> categories = adminCategoriesService.getAllCategories(); //商品情報が products リストに格納される
		// ビュー内で商品カテゴリーのリストを表示
		model.addAttribute("categories", categories);
		// フォームの初期化
		model.addAttribute("category", new CategoryRequest()); // 空のCategoryオブジェクトを追加
		model.addAttribute("updateCategory", new CategoryRequest()); // 空のCategoryオブジェクトを追加

		// 商品カテゴリーページのテンプレートを返す
		return "admin/admin_categories";
	}

	@PostMapping("/addCategory")
	public String addCategory(@Valid @ModelAttribute("category") CategoryRequest categoryRequest,
			BindingResult result, Model model, HttpSession session) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}

		if (result.hasErrors()) {
			// デバッグ情報をログに出力
			System.out.println("Validation errors: " + result.getAllErrors());

			// ビュー内で商品カテゴリーのリストを表示
			model.addAttribute("categories", adminCategoriesService.getAllCategories());
			model.addAttribute("updateCategory", new CategoryRequest()); // 空のCategoryオブジェクトを追加

			// 商品カテゴリーページのテンプレートを返す
			return "admin/admin_categories";
		}
		// フォームから受け取ったカテゴリーをデータベースに保存
		adminCategoriesService.saveCategory(categoryRequest);
		return "redirect:/admin/categories";
	}

	// 更新機能の処理
	@PostMapping("/updateCategory/{categoryId}")
	public String updateCategory(@PathVariable Long categoryId,
			@Valid @ModelAttribute("updateCategory") CategoryRequest categoryRequest,
			BindingResult result, Model model, HttpSession session) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}

		if (result.hasErrors()) {
			// デバッグ情報をログに出力
			System.out.println("Validation errors: " + result.getAllErrors());

			// ビュー内で商品カテゴリーのリストを表示
			model.addAttribute("categories", adminCategoriesService.getAllCategories());
			model.addAttribute("category", new CategoryRequest()); // 空のCategoryオブジェクトを追加

			// 商品カテゴリーページのテンプレートを返す
			return "admin/admin_categories";
		}

		// カテゴリーを更新するロジックを実装
		adminCategoriesService.updateCategory(categoryId, categoryRequest);
		return "redirect:/admin/categories";
	}

	//カテゴリーを削除する処理
	@PostMapping("/deleteCategory/{categoryId}")
	public String deleteCategory(@PathVariable Long categoryId, HttpSession session) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}
		// カテゴリーをデータベースから削除するロジックを実装
		adminCategoriesService.deleteCategory(categoryId);
		return "redirect:/admin/categories";
	}

}
