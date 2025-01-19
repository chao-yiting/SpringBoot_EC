package com.example.demo.controller.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.request.RegisterRequest;
import com.example.demo.service.users.RegisterService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/register") //すべてのHTTPリクエストを/register にマッピング
public class RegisterController {

	@Autowired //依存性注入
	private RegisterService registerService; //RegisterServiceクラスを使えるようにする

	//ユーザー登録フォームを表示する
	@GetMapping
	public String showRegistrationForm(Model model) {
		//登録フォームで入力したユーザー情報をビューに渡せるように
		//model オブジェクトに "user" という名前で User オブジェクトを追加。
		model.addAttribute("user", new RegisterRequest());
		return "users/register";//ユーザー登録フォームの表示ビュー名を返す
	}

	//ユーザー情報を受け取り、データベースに保存する
	@PostMapping
	public String processRegistration(@Valid @ModelAttribute("user") RegisterRequest userRequest,
			BindingResult result) {

		//バリデーション追加
		if (result.hasErrors()) {
			// デバッグ情報をログに出力
			System.out.println("Validation errors: " + result.getAllErrors());
			return "users/register"; // バリデーションエラーがある場合は入力画面に戻る
		}

		registerService.saveRegisterUser(userRequest);

		return "redirect:/login"; //登録後にリダイレクト
	}
}
