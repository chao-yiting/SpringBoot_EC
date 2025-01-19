package com.example.demo.service.admin;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.MPrefectures;
import com.example.demo.entity.Orders;
import com.example.demo.repository.MConsumptionTaxRepository;
import com.example.demo.repository.MDeliveryDateRepository;
import com.example.demo.repository.MPostageRepository;
import com.example.demo.repository.MPrefecturesRepository;
import com.example.demo.repository.OrdersRepository;

@Service
public class AdminOrdersService {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private MConsumptionTaxRepository consumptionTaxRepository;

	@Autowired
	private MPostageRepository postageRepository;

	@Autowired
	private MPrefecturesRepository prefecturesRepository;

	@Autowired
	private MDeliveryDateRepository deliveryDateRepository;

	/*/すべての商品情報を取得し、コントローラに返す(元)
	public List<Orders> getAllOrders() { //すべての商品をデータベースから取得する
		return ordersRepository.findAll(); //データベース内のすべてのレコードを抽出してリストとして返す
	}*/

	// ユーザーIDに基づいて注文を取得し、注文IDごとに注文をまとめる処理
	public Map<Long, List<Orders>> getOrdersGroupedByOrderId() {
		List<Orders> orders = ordersRepository.findAll();
		// order_idをキーとして、注文をグルーピングします
		return orders.stream().collect(Collectors.groupingBy(Orders::getOrderId));
	}

	// 注文IDごとにアイテム合計金額を計算するメソッド
	public Map<Long, Integer> calculateItemTotalPriceByOrderId() {
		// ユーザーIDに基づいて注文リストを取得
		List<Orders> orders = ordersRepository.findAll();

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
	public Map<Long, Integer> getPostageByOrderId() {
		// ユーザーIDに基づいて注文リストを取得
		List<Orders> orders = ordersRepository.findAll();
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
	public Map<Long, Integer> calculateTotalPriceByOrderId() {
		// ユーザーIDに基づいて注文リストを取得
		List<Orders> orders = ordersRepository.findAll();
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
									System.out.println("合計金額" + itemTotal);

									/*/ 消費稅加算
									int consumptionTax = consumptionTaxRepository.findById(1L).orElseThrow()
											.getConsumptionTax();
									totalPrice += Math.round(totalPrice * consumptionTax / 100.0);
									System.out.println("合計金額(税込み)" + totalPrice);
									*/
									// 送料加算
									//int postage = postageRepository.findById(1L).orElseThrow().getPostage();

									// 注文IDごとの送料を取得
									Orders order = orderList.get(0); // 注文リストから一つの注文を取得して使用
									// 注文に紐づいた都道府県IDを取得
									MPrefectures prefecture = prefecturesRepository
											.findById((long) order.getPrefecturesId()).orElseThrow();
									// 都道府県IDに基づいて送料を取得
									int postage = postageRepository.findById(prefecture.getPostageId()).orElseThrow()
											.getPostage();

									System.out.println("送料" + postage);
									return itemTotal + postage;
								})));
	}

	//注文ステータスを更新するロジックを実装(OrderId)
	public void updateOrderStatus(Long orderId) {
		// 指定された注文IDで注文を検索
		List<Orders> orders = ordersRepository.findByOrderId(orderId);

		// 注文リストから取得した全ての注文に対して新しいステータスを設定
		for (Orders order : orders) {
			order.setOrderStatus(1);
		}
		// 全ての注文を保存してデータベースを更新
		ordersRepository.saveAll(orders);
	}

	// 注文IDごとに到着予定日を計算するメソッド
	public Map<Long, String> calculatedDeliveryDate() {
		List<Orders> orders = ordersRepository.findAll();
		//Stream API を使用して注文を注文IDごとにグループ化し、各グループの合計金額を計算
		return orders.stream()
				.collect(Collectors.groupingBy(Orders::getOrderId,
						Collectors.collectingAndThen(
								Collectors.toList(),
								orderList -> {
									// 注文IDごとに最新の更新日を取得
									Timestamp latestUpdate = orderList.stream()
											.map(Orders::getUpdatedAt)
											.max(Comparator.naturalOrder())//更新日を標準の時系列順序で比較して、最大の日時を取得
											.orElse(Timestamp.valueOf(LocalDateTime.now()));//最新の更新日がない場合は現在の日時を使用

									// 注文IDごとに加算する日数を計算
									MPrefectures prefecture = prefecturesRepository
											.findById((long) orderList.get(0).getPrefecturesId()).orElseThrow();
									int deliveryDays = deliveryDateRepository.findById(prefecture.getDeliveryDateId())
											.orElseThrow().getDeliveryDate();

									//int deliveryDays = 3; // 例: 3日後に到着予定

									// 到着予定日を計算してフォーマット
									LocalDateTime deliveryDate = latestUpdate.toLocalDateTime().plusDays(deliveryDays);
									return DateTimeFormatter.ofPattern("yyyy/MM/dd").format(deliveryDate);
								})));
	}

	/*/注文ステータスを更新するロジックを実装(IDごと)
	public void updateOrderStatus(Long orderId) {
		// 指定された注文IDで注文を検索
		Orders order = ordersRepository.findById(orderId).orElse(null);
		order.setOrderStatus(1);// 新しいステータスを設定
		ordersRepository.save(order);// 注文を保存してデータベースを更新
	}*/
}
