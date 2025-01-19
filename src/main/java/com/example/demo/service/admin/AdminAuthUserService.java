package com.example.demo.service.admin;

import org.springframework.stereotype.Service;

import com.example.demo.entity.User;

import jakarta.servlet.http.HttpSession;

@Service
public class AdminAuthUserService {

	//一般的ではないが、定義したら引数の記述省略可能になる
	/*@Autowired
	private HttpSession session;*/
	
	public String adminAuthUser(HttpSession session) {
		// セッションからユーザー情報を取得
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		// ユーザーがログインしていないか、ユーザーオーソリティが1でない場合、リダイレクト
		if (loggedInUser == null || loggedInUser.getUserAuthority() != 1) {
			return "redirect:/login";
		}
		return null; // 認証が成功した場合
	}
}