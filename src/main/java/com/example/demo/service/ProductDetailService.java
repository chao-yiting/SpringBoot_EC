package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Products;
import com.example.demo.repository.ProductsRepository;

@Service
public class ProductDetailService {

	@Autowired //依存性注入
	private ProductsRepository productsRepository; //ProductsRepository クラスを使えるようにする

	//商品をIDで検索する
	public Products findById(Long productId) {
		// ProductRepositoryを使用して商品をIDで検索し、存在しない場合はnullを返す
		return productsRepository.findById(productId).orElse(null); // 必要に応じてエラーハンドリングを追加
	}

}
