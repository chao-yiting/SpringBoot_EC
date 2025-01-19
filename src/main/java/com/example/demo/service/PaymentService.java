package com.example.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.MPrefectures;
import com.example.demo.entity.Payment;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.MConsumptionTaxRepository;
import com.example.demo.repository.MPostageRepository;
import com.example.demo.repository.MPrefecturesRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.request.PaymentRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Token;

@Service
public class PaymentService {
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private MConsumptionTaxRepository consumptionTaxRepository;
	@Autowired
	private MPostageRepository postageRepository;
	@Autowired
	private MPrefecturesRepository prefecturesRepository;

	public void savePayment(PaymentRequest paymentRequest, Long userId) {
		// PaymentRequestからPaymentエンティティを作成し、ユーザーIDを設定
		Payment payment = new Payment();

		// 文字列からintに変換して設定
		payment.setCardNumber(Long.parseLong(paymentRequest.getCardNumber()));
		payment.setCardKind(Integer.parseInt(paymentRequest.getCardKind()));
		payment.setExpirationYear(Integer.parseInt(paymentRequest.getExpirationYear()));
		payment.setExpirationMonth(Integer.parseInt(paymentRequest.getExpirationMonth()));
		payment.setZipCode1(Integer.parseInt(paymentRequest.getZipCode1()));
		payment.setZipCode2(Integer.parseInt(paymentRequest.getZipCode2()));

		// その他のフィールドを設定
		payment.setCardHolder(paymentRequest.getCardHolder());
		payment.setFirstName(paymentRequest.getFirstName());
		payment.setLastName(paymentRequest.getLastName());
		payment.setAddress(paymentRequest.getAddress());
		payment.setUserId(userId); // ユーザーIDを設定

		//paymentRequest.setUserId(userId);

		// Paymentエンティティを保存
		paymentRepository.save(payment);
	}

	// ユーザーのカート内の商品リストを取得するメソッド
	public List<Cart> getCartItemsByUserId(Long userId) {
		return cartRepository.findByUserId(userId);
	}

	//合計金額を計算するメソッド
	public int calculatePriceWithTax(Long userId) {
		// ユーザーIDに基づいてカート内の商品を取得
		List<Cart> cartItems = getCartItemsByUserId(userId);
		int priceWithTax = 0;

		// カート内の商品ごとにループして、各商品の合計金額を計算して合計金額に加算
		for (Cart cartItem : cartItems) {
			priceWithTax += cartItem.getProduct().getPrice() * cartItem.getQuantity();
		}

		/*/ 消費税を取得
		MConsumptionTax consumptionTax = consumptionTaxRepository.findById(1L).orElse(new MConsumptionTax()); 
		// 1Lは消費税のIDに置き換える必要があります。// デフォルトのMConsumptionTaxを設定
		
		if (consumptionTax != null) {
			double taxAmount = totalPrice * (consumptionTax.getConsumptionTax() / 100.0); // 消費税を計算
			totalPrice += (int) taxAmount; // 合計金額に消費税を加算
		}*/
		int consumptionTax = consumptionTaxRepository.findById(1L).orElseThrow().getConsumptionTax();

		priceWithTax += Math.round(priceWithTax * consumptionTax / 100.0);

		return priceWithTax;
	}

	public int getPostage(Long userId) {
		// 都道府県IDに基づいて送料を取得
		//MPrefectures prefecture = prefecturesRepository.findById(prefectureId).orElseThrow();
		//int postage = postageRepository.findById(prefecture.getPostageId()).orElseThrow().getPostage();

		int postage = postageRepository.findById(1L).orElseThrow().getPostage();

		return postage;
	}

	public List<MPrefectures> getAllPrefectures() {
		return prefecturesRepository.findAll();
	}

	public int calculateTotalPrice(Long userId) {
		// 合計金額を取得
		int priceWithTax = calculatePriceWithTax(userId);
		// 送料を取得
		int postage = getPostage(userId);

		int totalPrice = priceWithTax + postage;
		return totalPrice;
	}

	//決済システム
	@Value("${stripe.api.secretKey}")
	private String secretKey;

	public PaymentIntent createStripePaymentIntent(PaymentRequest paymentRequest, Long userId) throws StripeException {
		// Stripe支払い情報の作成
		Stripe.apiKey = secretKey;
		List<String> paymentMethodTypes = new ArrayList<>();
		paymentMethodTypes.add("card");

		// Stripe APIを使用してトークンを解析し、カード情報を取得する
		Token token = Token.retrieve(paymentRequest.getStripeToken());
		String cardNumber = token.getCard().getLast4();
		String expirationYear = String.valueOf(token.getCard().getExpYear());
		String expirationMonth = String.valueOf(token.getCard().getExpMonth());
		// カードのブランドを取得し、数値に変換してcardKindに格納する
		String brand = token.getCard().getBrand();
		int cardKind;
		switch (brand) {
		case "Visa":
			cardKind = 1;
			break;
		case "MasterCard":
			cardKind = 2;
			break;
		case "JCB":
			cardKind = 3;
			break;
		default:
			cardKind = 0; // 未知のブランドの場合、0または適切なデフォルト値を設定する
		}

		// PaymentRequestにカード情報を設定する
		paymentRequest.setCardNumber(cardNumber);
		paymentRequest.setExpirationYear(expirationYear.substring(2)); // 最後の2桁のみ取得
		paymentRequest.setExpirationMonth(expirationMonth);
		paymentRequest.setCardKind(String.valueOf(cardKind));

		// 合計總金額（税込み）
		int totalPrice = calculatePriceWithTax(userId);

		//注文に紐づいた都道府県IDを取得
		MPrefectures prefecture = prefecturesRepository
				.findById(Long.parseLong(paymentRequest.getPrefecturesId())).orElseThrow();
		// 都道府県IDに基づいて送料を取得
		int postage = postageRepository.findById(prefecture.getPostageId()).orElseThrow()
				.getPostage();

		// 支払金額計算（アイテム合計+送料）
		int amount = totalPrice + postage;

		// 支払い情報の作成
		Map<String, Object> params = new HashMap<>();
		params.put("amount", amount);// 支払い金額
		params.put("currency", "JPY");// 通貨（日本円）
		params.put("payment_method_types", Arrays.asList("card"));// 支払い方法（カード）

		// カード情報の設定
		Map<String, Object> paymentMethodData = new HashMap<>();
		paymentMethodData.put("type", "card"); // 支払い方法のタイプ
		paymentMethodData.put("card[token]", paymentRequest.getStripeToken()); // カードトークン
		params.put("payment_method_data", paymentMethodData); // 支払い方法の詳細情報を設定
		// Stripe APIに支払い情報を送信して支払い処理を行う
		return PaymentIntent.create(params);
	}

}
