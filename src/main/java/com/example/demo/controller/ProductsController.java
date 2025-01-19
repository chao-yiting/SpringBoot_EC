package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Category;
import com.example.demo.entity.Products;
import com.example.demo.service.ProductsService; // ProductServiceをインポート

@Controller
public class ProductsController {

	@Autowired //依存性注入
	private ProductsService productsService; // ProductsServiceクラスを使えるようにする

	//商品一覧ページを表示する(GETリクエスト-productsにアクセスされたら起動)
	@GetMapping("/products")/*/だけにすると/products消える*/
	public String ProductsList(Model model) { //Model: ビューにデータを渡す
		// 商品情報をServiceから取得
		List<Products> products = productsService.getAllProducts(); //商品情報が products リストに格納される
		// 商品情報をビューに渡す
		model.addAttribute("products", products);

		// 商品カテゴリーリストを取得する
		List<Category> categories = productsService.getAllCategories(); // カテゴリー情報を取得するメソッド
		model.addAttribute("categories", categories);

		// 商品一覧ページのテンプレートを返す
		return "products";
	}

	// 商品名での部分一致検索を行う
	@GetMapping("/products/search")
	public String searchProducts(@RequestParam String searchTerm, Model model) {
		List<Products> products = productsService.searchProductsByTerm(searchTerm);// 検索結果を格納するためのリスト
		// 検索キーワードが指定されている場合、部分一致で商品を検索し、結果を products リストに格納
		model.addAttribute("products", products); // ビューに商品リストを渡す
		// 商品カテゴリーリストを取得する
		model.addAttribute("categories", productsService.getAllCategories());
		return "products"; // 商品一覧ページを表示する
	}
}
