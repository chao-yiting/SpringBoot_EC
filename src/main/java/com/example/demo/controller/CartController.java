package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Products;
import com.example.demo.entity.User;
import com.example.demo.request.CartRequest;
import com.example.demo.service.CartService;
import com.example.demo.service.ProductsService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class CartController {
	@Autowired
	private CartService cartService;

	@Autowired
	private ProductsService productsService;

	@GetMapping("/cart")
	public String viewCart(HttpSession session, Model model) {

		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			// ユーザーがログインしていない場合の処理
			return "redirect:/login"; // ログインページにリダイレクト
		}
		// ユーザーがログインしている場合の処理
		Long userId = loggedInUser.getId();

		/*短くしたがエラーが発生した
		 * session.getAttribute("loggedInUser")がnullである場合にはNullPointerExceptionが発生
		Long userId = ((User) session.getAttribute("loggedInUser")).getId();
		if (userId == null) {
		    // ユーザーがログインしていない場合の処理
		    return "redirect:/login"; // ログインページにリダイレクト
		}*/

		List<Cart> cartItems = cartService.getCartItemsByUserId(userId);

		int totalPrice = cartService.calculateTotalPrice(userId);

		model.addAttribute("cartItems", cartItems);
		model.addAttribute("totalPrice", totalPrice);

		// CartRequestフォームオブジェクトを追加
		model.addAttribute("updateCartItem", new CartRequest());

		return "cart";
	}

	// カートに商品を追加する
	@PostMapping("/addToCart")
	public String addToCart(@Valid @ModelAttribute("addCartItem") CartRequest cartRequest, BindingResult result,
			HttpSession session, Model model) {
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			// ユーザーがログインしていない場合の処理
			return "redirect:/login"; // ログインページにリダイレクト
		}

		// ユーザーがログインしている場合の処理
		Long userId = loggedInUser.getId();

		// バリデーションエラーがあるかチェック
		if (result.hasErrors()) {
			// デバッグ情報をログに出力
			System.out.println("Validation errors: " + result.getAllErrors());

			model.addAttribute("product", productsService.getProductById(cartRequest.getProductId()));
			return "product_detail";
		}

		// バリデーションが成功した場合
		// カートに追加する前に、在庫数をチェックする
		Products products = productsService.getProductById(cartRequest.getProductId());
		if (products != null && cartRequest.getQuantity() <= products.getStock()) {
			cartService.addToCart(userId, cartRequest);

			return "redirect:/cart"; // カートページにリダイレクト
		} else {
			// 在庫が足りない場合はエラーを表示して詳細ページに戻る
			model.addAttribute("error", "在庫が不足しています");
			// 商品情報を再度送る（詳細ページで表示するため）
			model.addAttribute("product", products);
			return "product_detail"; // 商品詳細ページに戻る
		}
	}

	// 更新機能の処理
	@PostMapping("/updateCartItemQuantity")
	public String updateCartItemQuantity(@Valid @ModelAttribute("updateCartItem") CartRequest cartRequest,
			BindingResult result, HttpSession session, Model model) {
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) { // ユーザーがログインしていない場合の処理
			return "redirect:/login"; // ログインページにリダイレクト
		}
		// ユーザーがログインしている場合の処理
		Long userId = loggedInUser.getId();

		// バリデーションエラーがあるかチェック
		if (result.hasErrors()) {
			// デバッグ情報をログに出力
			System.out.println("Validation errors: " + result.getAllErrors());

			model.addAttribute("cartItems", cartService.getCartItemsByUserId(userId));
			model.addAttribute("totalPrice", cartService.calculateTotalPrice(userId));
			return "cart";
		}

		cartService.updateCartItemQuantity(userId, cartRequest);
		return "redirect:/cart";
	}

	//削除ボタンがクリックされると、指定した商品を削除する処理を実行する	
	@PostMapping("/removeCartItem/{productId}")
	public String removeCartItem(@PathVariable Long productId, HttpSession session) {
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login";
		}
		Long userId = loggedInUser.getId();

		cartService.removeCartItem(userId, productId);
		return "redirect:/cart";
	}

	//支払いページに遷移する時、在庫数をチェックして在庫不足の場合はエラーメッセージを表示
	@PostMapping("/checkStocks")
	public String checkStocks(HttpSession session, Model model) {
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login";
		}
		// ユーザーがログインしている場合の処理
		Long userId = loggedInUser.getId();

		// カート内の商品リストを取得
		List<Cart> cartItems = cartService.getCartItemsByUserId(userId);

		// カート内の商品の在庫をチェック
		//在庫不足を判定するためのフラグを初期化
		boolean insufficientStock = false;
		// 不足している商品のリストを初期化
		List<Products> insufficientProducts = new ArrayList<>();

		//cartItems内の各Cartオブジェクトに対して、ループを使用して順番に処理
		for (Cart cartItem : cartItems) {
			//ループで処理している商品に関する情報が得る
			Products product = productsService.getProductById(cartItem.getProductId());
			//商品情報が存在しないか、またはカート内の数量が在庫数を上回る場合は在庫不足と判定
			if (product == null || cartItem.getQuantity() > product.getStock()) {
				insufficientStock = true;
				insufficientProducts.add(product); // 不足している商品をリストに追加
				//break;
			}
		}

		if (insufficientStock) {
			// 在庫が足りない場合はエラーを表示してカートページに留まる
			model.addAttribute("error", "カート内の商品の在庫が足りません");
			// カート情報を再度送る（カートページで表示するため）
			model.addAttribute("cartItems", cartItems);
			//合計金額を再度送る（カートページで表示するため）
			model.addAttribute("totalPrice", cartService.calculateTotalPrice(userId));
			// 不足している商品のリストをモデルに追加
			model.addAttribute("insufficientProducts", insufficientProducts);

			// CartRequestフォームオブジェクト再度追加（カートページで表示するため）
			model.addAttribute("updateCartItem", new CartRequest());

			return "cart"; // カートページに戻る
		} else {
			return "redirect:/payment";
		}
	}
}