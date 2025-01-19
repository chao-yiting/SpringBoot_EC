package com.example.demo.repository;

import java.util.List; // Listを使用するためにimportが必要

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Products;

//JpaRepositoryで、データベースへの操作を簡単に行うためのメソッドを提供する。
//Products エンティティを操作するためのメソッドが自動的に生成される。
public interface ProductsRepository extends JpaRepository<Products, Long> {
	
	 // 商品名を部分一致で検索するクエリメソッド
    // メソッド名から自動生成されるクエリ: SELECT * FROM Products WHERE name LIKE %searchTerm%
	List<Products> findByNameContaining(String searchTerm);

}