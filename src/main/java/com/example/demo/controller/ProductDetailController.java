package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entity.Category;
import com.example.demo.entity.Products;
import com.example.demo.request.CartRequest;
import com.example.demo.service.ProductDetailService;
import com.example.demo.service.ProductsService;

@Controller
@RequestMapping("/products")
public class ProductDetailController {

	@Autowired
	private ProductDetailService productDetailService; // ProductServiceを適切に注入する
	@Autowired //依存性注入
	private ProductsService productsService; // ProductsServiceクラスを使えるようにする

	// 商品詳細ページへのリクエストを処理
	@GetMapping("/product_detail/{productId}")
	public String showProductDetail(@PathVariable Long productId, Model model) {
		// 商品IDに基づいて商品情報をデータベースから取得
		Products product = productDetailService.findById(productId); // productServiceから商品情報を取得する
		//取得した商品情報をテンプレートに渡す
		model.addAttribute("product", product);

		// 商品カテゴリーリストを取得する
		List<Category> categories = productsService.getAllCategories(); // カテゴリー情報を取得するメソッド
		model.addAttribute("categories", categories);

		// CartRequestフォームオブジェクトを追加
		model.addAttribute("addCartItem", new CartRequest());

		
		// 商品詳細ページのテンプレート名を返す
		return "product_detail";
	}

}
