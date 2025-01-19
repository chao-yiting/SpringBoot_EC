package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
	Orders findByUserIdAndProductId(Long userId, Long productId);

	// ユーザーIDに基づいてカート内の商品リストを取得するメソッドを追加
	List<Orders> findByUserId(Long userId);

	//注文IDに基づいて注文リストを取得するメソッドを追加
	List<Orders> findByOrderId(Long orderId);
	//Optional はメソッドが値を返す際にnullを返すことを防ぐ
	//Optional<List<Orders>> findByOrderId(Long orderId);

	//List<Orders> findByPrefecturesId(Long PrefecturesId);
}