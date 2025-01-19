package com.example.demo.controller.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Orders;
import com.example.demo.service.admin.AdminAuthUserService;
import com.example.demo.service.admin.AdminOrdersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminOrdersController {

	@Autowired
	private AdminOrdersService adminOrdersService;

	@Autowired
	private AdminAuthUserService adminAuthUserService;

	@GetMapping("/admin/orders")
	public String orderList(HttpSession session, Model model, Long orderId) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}

		// ユーザーごとに注文をグループ化したMapを取得
		Map<Long, List<Orders>> groupedOrders = adminOrdersService.getOrdersGroupedByOrderId();
		// 注文IDごとにアイテム合計金額を計算
		Map<Long, Integer> itemTotalPrice = adminOrdersService.calculateItemTotalPriceByOrderId();
		//注文IDごとに送料を取得
		Map<Long, Integer> postage = adminOrdersService.getPostageByOrderId();
		// 注文IDごとの送料込み合計金額を計算
		Map<Long, Integer> totalPrices = adminOrdersService.calculateTotalPriceByOrderId();
		// 注文IDごとの到着予定日の計算を取得
		Map<Long, String> deliveryDate = adminOrdersService.calculatedDeliveryDate();

		// 注文を逆順にする
		List<Entry<Long, List<Orders>>> reversedEntries = new ArrayList<>(groupedOrders.entrySet());
		Collections.reverse(reversedEntries);

		// ビューにデータを渡す    
		model.addAttribute("itemTotalPrice", itemTotalPrice);
		model.addAttribute("postage", postage);
		model.addAttribute("totalPrices", totalPrices);
		model.addAttribute("deliveryDate", deliveryDate);
		model.addAttribute("groupedOrders", reversedEntries);

		return "admin/admin_orders";
	}

	//orderStatus変更
	@PostMapping("/admin/updateOrderStatus/{orderId}")
	public String updateOrderStatus(@PathVariable Long orderId, HttpSession session, Model model) {
		String authResult = adminAuthUserService.adminAuthUser(session);
		if (authResult != null) {
			return authResult;
		}
		// 注文ステータスを更新するロジックを実装
		adminOrdersService.updateOrderStatus(orderId);

		// ビューにデータを渡す    
		model.addAttribute("groupedOrders", adminOrdersService.getOrdersGroupedByOrderId());
		model.addAttribute("totalPrices", adminOrdersService.calculateTotalPriceByOrderId());
		model.addAttribute("deliveryDate", adminOrdersService.calculatedDeliveryDate());

		return "redirect:/admin/orders"; // 注文管理ページにリダイレクト
	}
}
