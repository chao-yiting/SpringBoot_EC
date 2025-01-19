package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Cart;


public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUserIdAndProductId(Long userId, Long productId);
	// ユーザーIDに基づいてカート内の商品リストを取得するメソッドを追加
    List<Cart> findByUserId(Long userId);
}