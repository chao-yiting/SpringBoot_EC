package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Orders;
import com.example.demo.entity.User;
import com.example.demo.service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {

	@Autowired
	private OrderService orderService;

	@GetMapping("/orders")
	public String orderList(HttpSession session, Model model) {
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			// ユーザーがログインしていない場合の処理
			return "redirect:/login"; // ログインページにリダイレクト
		}
		// ユーザーがログインしている場合の処理
		Long userId = loggedInUser.getId();
		//List<Orders> orders = orderService.getOrdersByUserId(userId);

		// ユーザーごとに注文をグループ化したMapを取得
		Map<Long, List<Orders>> groupedOrders = orderService.getOrdersGroupedByOrderId(userId);

		// 注文IDごとにアイテム合計金額を計算
		Map<Long, Integer> itemTotalPrice = orderService.calculateItemTotalPriceByOrderId(userId);
		//注文IDごとに送料を取得
		Map<Long, Integer> postage = orderService.getPostageByOrderId(userId);
		// 注文IDごとの送料込み合計金額を計算
		Map<Long, Integer> totalPrices = orderService.calculateTotalPriceByOrderId(userId);

		// 注文IDごとの到着予定日を計算
		Map<Long, String> deliveryDate = orderService.calculatedDeliveryDateByOrderId(userId);

		// エントリーを逆順にする
		List<Entry<Long, List<Orders>>> reversedEntries = new ArrayList<>(groupedOrders.entrySet());
		Collections.reverse(reversedEntries);

		model.addAttribute("groupedOrders", reversedEntries);

		// ビューにデータを渡す    
		//model.addAttribute("groupedOrders", groupedOrders);
		model.addAttribute("itemTotalPrice", itemTotalPrice);
		model.addAttribute("postage", postage);
		model.addAttribute("totalPrices", totalPrices);
		model.addAttribute("deliveryDate", deliveryDate); // 到着予定日を追加

		return "orders"; // 適切なView名を指定
	}
}
