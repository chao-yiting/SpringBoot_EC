package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Products;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.MConsumptionTaxRepository;
import com.example.demo.repository.ProductsRepository; // ProductRepositoryをインポート
import com.example.demo.request.CartRequest;

@Service
public class CartService {
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductsRepository productsRepository; // ProductRepositoryをインポート

	@Autowired
	private MConsumptionTaxRepository consumptionTaxRepository;

	// カートに商品を追加する
	public void addToCart(Long userId, CartRequest cartRequest) {/*Cart addCartItem*/
		// ユーザーのカートに商品を追加するロジック
		Cart cartItem = cartRepository.findByUserIdAndProductId(userId, cartRequest.getProductId());
		//		Cart cartItem = cartRepository.findByUserIdAndProductId(userId, addCartItem.getProductId());

		if (cartItem == null) {
			// カートに新しい商品を追加
			Cart newCartItem = new Cart();
			newCartItem.setUserId(userId);
			newCartItem.setProductId(cartRequest.getProductId());
			newCartItem.setQuantity(cartRequest.getQuantity());
			cartRepository.save(newCartItem);

		} else {
			// 既にカートに同じ商品がある場合、数量を更新
			cartItem.setQuantity(cartItem.getQuantity() + cartRequest.getQuantity());
			cartRepository.save(cartItem);
		}
	}

	// ユーザーのカート内の商品リストを取得するメソッド
	public List<Cart> getCartItemsByUserId(Long userId) {
		return cartRepository.findByUserId(userId);
	}

	// productIdを使用してProductsテーブルから商品情報を取得する
	public Products getProductById(Long productId) {
		return productsRepository.findById(productId).orElse(null);
	}

	//カート内の合計金額を計算するメソッド
	public int calculateTotalPrice(Long userId) {
		List<Cart> cartItems = getCartItemsByUserId(userId);
		int itemsPrice = 0;

		//商品金額を計算
		for (Cart cartItem : cartItems) {
			itemsPrice += cartItem.getProduct().getPrice() * cartItem.getQuantity();
		}

		/*/ 消費税を取得
		MConsumptionTax consumptionTax = consumptionTaxRepository.findById(1L).orElse(new MConsumptionTax()); // 1Lは消費税のIDに置き換える必要があります
		double taxAmount = itemsPrice * (consumptionTax.getConsumptionTax() / 100.0); // 消費税を計算
		// 合計金額に消費税を加算
		int totalPrice = (int) (itemsPrice + taxAmount);*/

		// 消費税を取得して加算
		int consumptionTax = consumptionTaxRepository.findById(1L).orElseThrow().getConsumptionTax();
		int totalPrice = (int) (itemsPrice +Math.round(itemsPrice * consumptionTax / 100.0));

		return totalPrice;
	}

	//カート内の合計金額(分けて表示)を計算するメソッド
	/*	public int calculateItemsPrice(Long userId) {
			List<Cart> cartItems = getCartItemsByUserId(userId);
			int itemsPrice = 0;
	
			// 拡張for文で繰り返し処理で商品金額を計算
			for (Cart cartItem : cartItems) {
				itemsPrice += cartItem.getProduct().getPrice() * cartItem.getQuantity();
			}
			return itemsPrice;
		}
	
		// 消費税を取得
		public int calculateTaxAmount(Long userId) {
			int itemsPrice = calculateItemsPrice(userId);
	//		MConsumptionTax consumptionTax = consumptionTaxRepository.findById(1L).orElse(new MConsumptionTax()); // 1Lは消費税のIDに置き換える必要があります
	//		double taxAmount = itemsPrice * (consumptionTax.getConsumptionTax() / 100.0); // 消費税を計算
	//		MConsumptionTax consumptionTax = consumptionTaxRepository.findById(1L).orElse(null);
	//		double  taxAmount = consumptionTax != null ? consumptionTax.getConsumptionTax() / 100.0 : 0.0;
	
			MConsumptionTax consumptionTax = consumptionTaxRepository.findById(1L).orElse(null);// 1Lは消費税のIDに置き換える必要があります
			double taxAmount = itemsPrice * (consumptionTax.getConsumptionTax() / 100.0); // 消費税を計算
			return (int) taxAmount;
		}
	*/
	/*/ 消費税を取得
	MConsumptionTax consumptionTax = consumptionTaxRepository.findById(1L).orElse(null);
	double taxRate = consumptionTax != null ? consumptionTax.getConsumptionTax() / 100.0 : 0.0;
	
	// 消費税を計算
	double taxAmount = itemsPrice * taxRate;
	*/

	/*	
		public int calculateTotalPrice(Long userId) {
			int itemsPrice = calculateItemsPrice(userId);
			int taxAmount = calculateTaxAmount(userId);
			int totalPrice = (int) (itemsPrice + taxAmount); // 合計金額に消費税を加算
	
			return totalPrice;
		}
	*/

	// 商品数量を編集する
	public void updateCartItemQuantity(Long userId, CartRequest cartRequest) {/*Cart updateCartItem*/
		Cart cartItem = cartRepository.findByUserIdAndProductId(userId, cartRequest.getProductId());

		cartItem.setQuantity(cartRequest.getQuantity());
		cartRepository.save(cartItem);

	}

	// 商品をIDで削除するメソッド
	public void removeCartItem(Long userId, Long productId) {
		// 特定のユーザーと商品に関連するカートアイテムを取得
		Cart cartItem = cartRepository.findByUserIdAndProductId(userId, productId);

		// カートアイテムが見つかった場合、削除
		cartRepository.delete(cartItem);

	}
}
