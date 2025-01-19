package com.example.demo.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.MConsumptionTax;
import com.example.demo.entity.MPrefectures;
import com.example.demo.entity.Orders;
import com.example.demo.entity.Products;
import com.example.demo.entity.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.MConsumptionTaxRepository;
import com.example.demo.repository.MDeliveryDateRepository;
import com.example.demo.repository.MPostageRepository;
import com.example.demo.repository.MPrefecturesRepository;
import com.example.demo.repository.OrdersRepository;
import com.example.demo.repository.ProductsRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.PaymentRequest;

@Service
public class OrderService {
	@Autowired
	private OrdersRepository orderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductsRepository productRepository;

	@Autowired
	private UserRepository userRepository; // Userエンティティを取得するためのリポジトリを追加

	@Autowired
	private MConsumptionTaxRepository consumptionTaxRepository;

	@Autowired
	private MPostageRepository postageRepository;

	@Autowired
	private MPrefecturesRepository prefecturesRepository;

	@Autowired
	private MDeliveryDateRepository deliveryDateRepository;

	//注文作成
	public void processOrder(PaymentRequest paymentRequest, Long userId) {
		User user = userRepository.findById(userId).orElse(null); // ユーザーを取得
		List<Cart> cartItems = cartRepository.findByUserId(userId);

		//生成した注文IDを使用して、一つの注文として扱う
		Long orderId = generateOrderId();

		// 消費税を取得
		MConsumptionTax consumptionTax = consumptionTaxRepository.findById(1L).orElse(new MConsumptionTax());

		cartItems.forEach(cartItem -> {
			// カートのアイテムを元にOrderを作成
			Orders order = new Orders();
			order.setUser(user);
			order.setProduct(cartItem.getProduct());
			order.setQuantity(cartItem.getQuantity());
			//税抜き合計金額計算で保存
			order.setTotalPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());

			/*/税込み合計金額計算で保存
			order.setTotalPrice((int) Math.round((cartItem.getProduct().getPrice() * cartItem.getQuantity())
					* (1 + (consumptionTax.getConsumptionTax() / 100.0))));*/

			// 都道府県IDを注文情報に保存
			order.setPrefecturesId(Integer.parseInt(paymentRequest.getPrefecturesId()));

			order.setOrderId(orderId); // 同じ注文IDを使用

			// 注文を保存
			orderRepository.save(order);

			// カートから削除
			cartRepository.delete(cartItem);

			// 商品の在庫数を更新
			Products product = cartItem.getProduct();
			product.setStock(product.getStock() - cartItem.getQuantity());
			productRepository.save(product);
		});
	}

	// 注文IDを生成するメソッド
	//実際はデータベースのシーケンスやUUIDを使用することが一般的。
	private Long generateOrderId() {
		List<Orders> orders = orderRepository.findAll();
		Optional<Long> maxOrderId = orders.stream()
				.map(Orders::getOrderId)
				.max(Comparator.naturalOrder());

		Long orderId = maxOrderId.orElse(0L) + 1; // もし値がない場合は0を使用して+1
		return orderId;
	}

	//注文履歴
	// ユーザーIDに基づいて注文を取得し、注文IDごとに注文をまとめる処理
	public Map<Long, List<Orders>> getOrdersGroupedByOrderId(Long userId) {
		List<Orders> orders = orderRepository.findByUserId(userId);
		// order_idをキーとして、注文をグルーピングします
		return orders.stream().collect(Collectors.groupingBy(Orders::getOrderId));
	}

	// 注文IDごとにアイテム合計金額を計算するメソッド
	public Map<Long, Integer> calculateItemTotalPriceByOrderId(Long userId) {
		// ユーザーIDに基づいて注文リストを取得
		List<Orders> orders = orderRepository.findByUserId(userId);

		//Stream API を使用して注文を注文IDごとにグループ化し、各グループの合計金額を計算
		return orders.stream()
				.collect(Collectors.groupingBy(Orders::getOrderId,
						Collectors.collectingAndThen(
								Collectors.toList(),
								orderList -> {
									// 注文IDごとに商品の合計金額を計算
									int itemTotal = orderList.stream()
											.mapToInt(order -> order.getTotalPrice())
											.sum();

									// 注文IDごとに消費税を取得して加算
									int consumptionTax = consumptionTaxRepository.findById(1L).orElseThrow()
											.getConsumptionTax();
									itemTotal += Math.round(itemTotal * consumptionTax / 100.0);

									return itemTotal;
								})));
	}

	//注文IDごとに送料を取得するメソッド
	public Map<Long, Integer> getPostageByOrderId(Long userId) {
		// ユーザーIDに基づいて注文リストを取得
		List<Orders> orders = orderRepository.findByUserId(userId);
		return orders.stream()
				.collect(Collectors.groupingBy(Orders::getOrderId,
						Collectors.collectingAndThen(
								Collectors.toList(),
								orderList -> {
									//Stream API を使用して注文を注文IDごとにグループ化し、各グループの合計金額を計算
									// 注文IDごとの送料を取得
									Orders order = orderList.get(0); // 注文リストから一つの注文を取得して使用
									// 注文に紐づいた都道府県IDを取得
									MPrefectures prefecture = prefecturesRepository
											.findById((long) order.getPrefecturesId()).orElseThrow();
									// 都道府県IDに基づいて送料を取得
									int postage = postageRepository.findById(prefecture.getPostageId()).orElseThrow()
											.getPostage();

									return postage;
								})));

	}

	// 注文IDごとに送料込み合計金額を計算するメソッド
	public Map<Long, Integer> calculateTotalPriceByOrderId(Long userId) {
		// ユーザーIDに基づいて注文リストを取得
		List<Orders> orders = orderRepository.findByUserId(userId);

		//Stream API を使用して注文を注文IDごとにグループ化し、各グループの合計金額を計算
		return orders.stream()
				.collect(Collectors.groupingBy(Orders::getOrderId,
						Collectors.collectingAndThen(
								Collectors.toList(),
								orderList -> {
									// 注文IDごとに商品の合計金額を計算
									int itemTotal = orderList.stream()
											.mapToInt(order -> order.getTotalPrice())
											.sum();

									// 注文IDごとに消費税を取得して加算
									int consumptionTax = consumptionTaxRepository.findById(1L).orElseThrow()
											.getConsumptionTax();
									itemTotal += Math.round(itemTotal * consumptionTax / 100.0);

									//System.out.println("合計金額" + itemTotle);

									// 注文IDごとの送料を取得
									Orders order = orderList.get(0); // 注文リストから一つの注文を取得して使用
									// 注文に紐づいた都道府県IDを取得
									MPrefectures prefecture = prefecturesRepository
											.findById((long) order.getPrefecturesId()).orElseThrow();
									// 都道府県IDに基づいて送料を取得
									int postage = postageRepository.findById(prefecture.getPostageId()).orElseThrow()
											.getPostage();

									//System.out.println("送料" + postage);
									return itemTotal + postage;
								})));
	}

	// 注文IDごとに到着予定日を計算するメソッド
	public Map<Long, String> calculatedDeliveryDateByOrderId(Long userId) {
		List<Orders> orders = orderRepository.findByUserId(userId);

		// Stream API を使用して注文を注文IDごとにグループ化し、各グループの到着予定日を計算
		return orders.stream()
				.collect(Collectors.groupingBy(Orders::getOrderId, // 注文IDごとにグループ化
						Collectors.collectingAndThen(// グループごとの処理
								Collectors.toList(), // グループ内の注文をリストにまとめる
								orderList -> {
									// 注文IDごとに最新の更新日を取得
									Timestamp latestUpdate = orderList.stream()
											.map(Orders::getUpdatedAt)
											.max(Comparator.naturalOrder())//更新日を標準の時系列順序で比較して、最大の日時を取得
											.orElse(Timestamp.valueOf(LocalDateTime.now()));//最新の更新日がない場合は現在の日時を使用

									// 注文IDごとに加算する日数を取得
									MPrefectures prefecture = prefecturesRepository
											.findById((long) orderList.get(0).getPrefecturesId()).orElseThrow();
									int deliveryDays = deliveryDateRepository.findById(prefecture.getDeliveryDateId())
											.orElseThrow().getDeliveryDate();

									// 到着予定日を計算してフォーマット
									LocalDateTime deliveryDate = latestUpdate.toLocalDateTime().plusDays(deliveryDays);
									return DateTimeFormatter.ofPattern("yyyy/MM/dd").format(deliveryDate);
								})));
	}
}
