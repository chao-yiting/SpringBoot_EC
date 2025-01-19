package com.example.demo.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Category;
import com.example.demo.entity.Products;
import com.example.demo.service.admin.AdminAuthUserService;
import com.example.demo.service.admin.AdminProductsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminProductsController {
	@Autowired //依存性注入
	private AdminProductsService adminProductsService; // AdminProductsServiceクラスを使えるようにする

	@Autowired
	private AdminAuthUserService adminAuthUserService;

	//商品一覧ページを表示する(GETリクエスト-productsにアクセスされたら起動)
	@GetMapping("/admin/products")
	public String ProductsList(Model model, HttpSession session) { //Model: ビューにデータを渡す
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}

		// 商品情報をServiceから取得
		List<Products> products = adminProductsService.getAllProducts(); //商品情報が products リストに格納される
		// 商品情報をビューに渡す
		model.addAttribute("products", products);

		// 商品追加フォームに渡すために、カテゴリーリストを取得する
		List<Category> categories = adminProductsService.getAllCategories(); // カテゴリー情報を取得するメソッド
		model.addAttribute("categories", categories); // カテゴリーリストをビューに渡す

		//model.addAttribute("addProduct", new ProductsRequest());

		// 商品一覧ページのテンプレートを返す
		return "admin/admin_products";
	}

	// 商品名での部分一致検索を行う
	@GetMapping("/admin/productsSearch")
	public String searchProducts(@RequestParam String searchTerm, Model model, HttpSession session) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}
		List<Products> products = adminProductsService.searchProductsByTerm(searchTerm);// 検索結果を格納するためのリスト
		// 検索キーワードが指定されている場合、部分一致で商品を検索し、結果を products リストに格納
		model.addAttribute("products", products); // ビューに商品リストを渡す

		// 商品追加フォームに渡すために、カテゴリーリストを取得する
		List<Category> categories = adminProductsService.getAllCategories(); // カテゴリー情報を取得するメソッド
		model.addAttribute("categories", categories);

		return "admin/admin_products"; // 商品一覧ページを表示する
	}
}
