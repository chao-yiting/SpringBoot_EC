package com.example.demo.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entity.Category;
import com.example.demo.request.ProductsRequest;
import com.example.demo.service.admin.AdminAuthUserService;
import com.example.demo.service.admin.AdminProductCreateService;
import com.example.demo.service.admin.AdminProductsService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/products")
public class AdminProductCreateController {

	@Autowired
	private AdminAuthUserService adminAuthUserService;

	@Autowired //依存性注入
	private AdminProductsService adminProductsService;

	@Autowired //依存性注入
	private AdminProductCreateService adminProductCreateService;

	@GetMapping("/create_product")
	public String showProductCreate(Model model,
			HttpSession session) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}
		// 商品追加フォームに渡すために、カテゴリーリストを取得する
		List<Category> categories = adminProductsService.getAllCategories(); // カテゴリー情報を取得するメソッド
		model.addAttribute("categories", categories); // カテゴリーリストをビューに渡す

		model.addAttribute("addProduct", new ProductsRequest());
		// 商品詳細ページのテンプレート名を返す
		return "admin/admin_product_create";
	}

	//商品を追加する(POSTリクエスト-ファームが送信されたら起動)
	@PostMapping("/addProduct")
	public String addProduct(@Valid @ModelAttribute("addProduct") ProductsRequest productsRequest,
			/*@RequestParam("imageFile") MultipartFile imageFile,*/
			BindingResult result, Model model, HttpSession session) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}
		//バリデーション追加
		if (result.hasErrors()) {
			// デバッグ情報をログに出力
			System.out.println("Validation errors: " + result.getAllErrors());

			// 商品情報、カテゴリー、フォームをビューに渡す（表示するため）
			model.addAttribute("categories", adminProductsService.getAllCategories()); // カテゴリーリストをビューに渡す

			// バリデーションエラーがある場合は入力画面に戻るため、再度フォームオブジェクトを追加する
			model.addAttribute("addProduct", productsRequest);

			return "admin/admin_product_create"; // バリデーションエラーがある場合は入力画面に戻る
		}
//新增圖片造成バリデーションエラー出不來@RequestParam跟@ModelAttribute可能不能一起用在問GPT
		/*try {
			// 画像を保存するロジック
			String imagePath = adminProductCreateService.saveImage(imageFile);
			// フォームから受け取った商品情報をデータベースに保存
			adminProductCreateService.saveProduct(productsRequest, imagePath);
			System.out.println("商品追加した");
		} catch (Exception e) {
			// 保存中にエラーが発生した場合の処理
			e.printStackTrace();
			// 商品情報、カテゴリー、フォームをビューに渡す（表示するため）
			model.addAttribute("categories", adminProductsService.getAllCategories()); // カテゴリーリストをビューに渡す

			// バリデーションエラーがある場合は入力画面に戻るため、再度フォームオブジェクトを追加する
			model.addAttribute("addProduct", productsRequest);

			return "admin/admin_product_create"; 
		}*/
		
		adminProductCreateService.saveProduct(productsRequest);
		System.out.println("商品追加した");

		return "redirect:/admin/products"; // 商品が追加された後、商品一覧ページにリダイレクト
	}
}
