package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.MPrefectures;
import com.example.demo.entity.User;
import com.example.demo.request.PaymentRequest;
import com.example.demo.service.OrderService;
import com.example.demo.service.PaymentService;
import com.stripe.exception.StripeException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PaymentController {
	@Autowired
	private PaymentService paymentService;

	@Autowired
	private OrderService orderService;

	@GetMapping("/payment")
	public String viewPayment(HttpSession session, Model model) {

		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			// ユーザーがログインしていない場合の処理
			return "redirect:/login"; // ログインページにリダイレクト
		}
		// ユーザーがログインしている場合の処理
		Long userId = loggedInUser.getId();

		// PaymentRequestオブジェクトをモデルに追加
		model.addAttribute("payment", new PaymentRequest());

		//税込み合計金額計算
		int priceWithTax = paymentService.calculatePriceWithTax(userId);
		model.addAttribute("priceWithTax", priceWithTax);

		//送料表示
		int postage = paymentService.getPostage(userId);
		model.addAttribute("postage", postage);

		// 都道府県のリストを取得
		List<MPrefectures> prefectures = paymentService.getAllPrefectures();
		model.addAttribute("prefectures", prefectures);

		//合計金額計算
		int totalPrice = paymentService.calculateTotalPrice(userId);
		model.addAttribute("totalPrice", totalPrice);

		return "payment";

	}

	@PostMapping("/processPayment")
	public String processPayment(@Valid @ModelAttribute("payment") PaymentRequest paymentRequest,
			BindingResult result, HttpSession session, Model model) {
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login";// ログインしていない場合はログインページにリダイレクト
		}
		Long userId = loggedInUser.getId();

		if (result.hasErrors()) {
			// バリデーションエラーがある場合は再計算してビューに渡す
			model.addAttribute("priceWithTax", paymentService.calculatePriceWithTax(userId));
			model.addAttribute("postage", paymentService.getPostage(userId));
			model.addAttribute("totalPrice", paymentService.calculateTotalPrice(userId));
			model.addAttribute("prefectures", paymentService.getAllPrefectures());
			// 選択された都道府県をセッションに保存
			//session.setAttribute("selectedPrefectureId", paymentRequest.getPrefecturesId());
			System.out.println(paymentRequest.getPrefecturesId());
			return "payment"; // バリデーションエラーがある場合は入力画面に戻る
		}

		// バリデーションが成功した場合
		//決済システム追加
		try {
			// Stripe支払い処理を行う
			paymentService.createStripePaymentIntent(paymentRequest, userId);
			//  Stripe支払い処理が成功した場合、支払い情報を保存
			paymentService.savePayment(paymentRequest, userId);
			//注文情報を生成(カート内の商品をOrdersテーブルに移動)
			orderService.processOrder(paymentRequest, userId);
			return "redirect:/thanks"; // 成功した場合の遷移先を指定
		} catch (StripeException e) {
			model.addAttribute("errorMessage", "決済でエラーが発生しました。");
			System.out.println("エラー");
			return "payment"; // エラーの場合のレスポンスreturn "error"
		}
	}


}
