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
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entity.Category;
import com.example.demo.entity.Products;
import com.example.demo.request.ProductsRequest;
import com.example.demo.service.admin.AdminAuthUserService;
import com.example.demo.service.admin.AdminProductDetailService;
import com.example.demo.service.admin.AdminProductsService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/products")
public class AdminProductsDetailController {
	@Autowired
	private AdminProductDetailService adminProductDetailService; // ProductServiceを適切に注入する

	@Autowired
	private AdminAuthUserService adminAuthUserService;

	@Autowired
	private AdminProductsService adminProductsService;

	// 商品詳細ページへのリクエストを処理
	@GetMapping("/product_detail/{productId}")
	public String showProductDetail(@PathVariable Long productId, Model model,
			HttpSession session) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}

		// 商品IDに基づいて商品情報をデータベースから取得
		Products product = adminProductDetailService.findById(productId);
		// 取得した商品情報をテンプレートに渡す
		model.addAttribute("product", product);

		// 商品追加フォームに渡すために、カテゴリーリストを取得する
		List<Category> categories = adminProductsService.getAllCategories(); // カテゴリー情報を取得するメソッド
		model.addAttribute("categories", categories); // カテゴリーリストをビューに渡す

		//フォームに対するリクエストを処理するための新しい ProductsRequest インスタンスを作成
		model.addAttribute("updateProduct", new ProductsRequest());

		// 商品詳細ページのテンプレート名を返す
		return "admin/admin_product_detail";
	}

	// 更新機能の処理
	@PostMapping("/updateProduct")
	public String updateProduct(@Valid @ModelAttribute("updateProduct") ProductsRequest productsRequest,
			/*@RequestParam("imageFile") MultipartFile imageFile,*/
			Products product, BindingResult result, Model model) {
		// バリデーションエラーがあるかどうかを確認
		if (result.hasErrors()) {
			// デバッグ情報をログに出力
			System.out.println("Validation errors: " + result.getAllErrors());

			// 取得した商品情報をテンプレートに渡す
			model.addAttribute("product", adminProductDetailService.findById(product.getId()));
			// 商品追加フォームに渡すために、カテゴリーリストを取得する
			model.addAttribute("categories", adminProductsService.getAllCategories()); // カテゴリーリストをビューに渡す
			// バリデーションエラーがある場合、フォームに入力された値とエラーメッセージを保持して再表示する
			model.addAttribute("updateProduct", productsRequest);
			return "admin/admin_product_detail";
		}

		adminProductDetailService.updateProduct(product, productsRequest);
		/*try {
			// 画像がアップロードされた場合のみ、画像を更新する
			if (imageFile != null && !imageFile.isEmpty()) {
				// 商品情報を更新する
				adminProductDetailService.updateProduct(product, productsRequest, imageFile);
			} else {
				// 画像がアップロードされない場合、画像を更新せずに商品情報のみを更新する
				adminProductDetailService.updateProduct(product, productsRequest, null);
			}
		} catch (Exception e) {
			// 保存中にエラーが発生した場合の処理
			e.printStackTrace();
			// エラーメッセージを設定してフォームを再表示するなどの処理を行う
			// 取得した商品情報をテンプレートに渡す
			model.addAttribute("product", adminProductDetailService.findById(product.getId()));
			// 商品追加フォームに渡すために、カテゴリーリストを取得する
			model.addAttribute("categories", adminProductsService.getAllCategories()); // カテゴリーリストをビューに渡す
			// バリデーションエラーがある場合、フォームに入力された値とエラーメッセージを保持して再表示する
			model.addAttribute("updateProduct", productsRequest);

			return "admin/admin_product_detail";
		}*/

		// 商品情報を更新した後、商品詳細ページにリダイレクト
		return "redirect:/admin/products/product_detail/" + product.getId();
	}

	//削除ボタンがクリックされると、指定した商品を削除する処理を実行する
	@PostMapping("/deleteProduct/{productId}")
	public String deleteProduct(@PathVariable Long productId) {
		// 商品をデータベースから削除するロジックを実装
		adminProductDetailService.deleteProduct(productId);
		return "redirect:/admin/products"; // 削除後、商品一覧ページにリダイレクト
	}

}
