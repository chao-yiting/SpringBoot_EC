package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Products;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductsRepository;

@Service //ビジネスロジックを提供する
public class ProductsService {

	@Autowired //依存性注入
	private ProductsRepository productsRepository; //ProductsRepository クラスを使えるようにする
	@Autowired
	private CategoryRepository categoryRepository; // CategoriesRepositoryを利用できるようにする

	//すべての商品情報を取得し、コントローラに返す
	public List<Products> getAllProducts() { //すべての商品をデータベースから取得する
		return productsRepository.findAll(); //データベース内のすべてのレコードを抽出してリストとして返す
	}

	//商品を部分一致で検索する
	public List<Products> searchProductsByTerm(String searchTerm) {
		// ProductsRepositoryを使用して部分一致で商品を検索するクエリを実行
		return productsRepository.findByNameContaining(searchTerm);
	}

	// 指定されたIDの商品情報を取得する
	public Products getProductById(Long productId) {
		return productsRepository.findById(productId).orElse(null);
	}

	// カテゴリー情報を取得するメソッド
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}
}
