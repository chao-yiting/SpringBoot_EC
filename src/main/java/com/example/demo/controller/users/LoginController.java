package com.example.demo.controller.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes; // セッション管理追加
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.User;
import com.example.demo.request.LoginRequest;
import com.example.demo.service.users.LoginService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@SessionAttributes("loggedInUser") // セッション管理追加
public class LoginController {
	private final LoginService loginService;

	@Autowired
	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	// ログインフォームを表示する
	@GetMapping("/login")
	public String showLoginForm(Model model) {
		// "user" オブジェクトを設定
		model.addAttribute("user", new LoginRequest());
		return "users/login";
	}

	// ログイン情報を受け取り、ログイン処理を行う
	@PostMapping("/login")
	public String login(@Valid @ModelAttribute("user") LoginRequest userRequest, BindingResult result, Model model,
			RedirectAttributes redirectAttributes, HttpSession session) { //ログイン失敗時にエラーメッセージをリダイレクト先のログインフォームに渡す
		//バリデーション
		if (result.hasErrors()) {
			// デバッグ情報をログに出力
			System.out.println("Validation errors: " + result.getAllErrors());
			// エラーがある場合はログイン画面に戻る
			return "users/login";
		}
		// ユーザーの認証を行い、認証されたユーザーオブジェクトを取得する
		User authenticatedUser = loginService.authenticateUser(userRequest);

		if (authenticatedUser != null) {
			System.out.println("成功");
			// ログイン成功時の処理を実行
			// ここでユーザーセッションなどを設定する（セッションの設定、ダッシュボードへのリダイレクトなど）
			session.setAttribute("loggedInUser", authenticatedUser); // ユーザーをセッションに保存

			// ユーザーの権限（user_authority）に基づいてリダイレクト先を設定
			if (authenticatedUser.getUserAuthority() == 0) {
				return "redirect:/"; // 一般ユーザーの場合は/topにリダイレクト
			} else if (authenticatedUser.getUserAuthority() == 1) {
				session.setAttribute("isAdmin", true); // 管理者の場合はisAdmin属性をセッションに保存
				return "redirect:/admin/top"; // 管理者の場合は/admin/topにリダイレクト
			}

		}
		System.out.println("失敗");
		// ログイン失敗時の処理を実行
		// エラーメッセージをリダイレクト先に渡します
		redirectAttributes.addFlashAttribute("errorMessage", "無効なメールアドレスまたはパスワード"); //Invalid email or password.
		return "redirect:/login"; //ログインフォームページにリダイレクト

	}

	//ログアウトのセッション完了
	@GetMapping("/logout")
	public String logout(SessionStatus sessionStatus, HttpSession session) {
		//@SessionAttributes アノテーションと連携して、指定した属性をクリアする
		// セッションを明示的に完了させる
		sessionStatus.setComplete();
		// 管理者フラグのセッション属性を削除する
		session.removeAttribute("isAdmin");
		return "redirect:/"; // ログアウト後のリダイレクト先を指定
	}
}
