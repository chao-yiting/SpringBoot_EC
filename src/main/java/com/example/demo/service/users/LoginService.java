package com.example.demo.service.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.LoginRequest;
import com.example.demo.util.PasswordEncoder;

@Service //ビジネスロジックを提供する
public class LoginService {

	@Autowired //依存性注入
	private UserRepository userRepository; //UserRepositoryクラスを使えるようにする
	/*	@Autowired
		private BCryptPasswordEncoder passwordEncoder;// BCryptPasswordEncoderのインスタンスを注入②
		*/

	//ユーザー認証
	//渡されたメールアドレスとパスワードを使用してユーザー認証を行う
	public User authenticateUser(LoginRequest userRequest) {

		// ユーザーの認証ロジック
		// ユーザーを取得し、パスワードの一致を確認
		User existingUser = userRepository.findBymailAddress(userRequest.getMailAddress());
		//元のログイン確認(バッシュ化される前のユーザー)
		if (existingUser != null && existingUser.getPassword().equals(userRequest.getPassword())) {
			return existingUser;
		}
		//バッシュ化パスワードの場合①
		if (existingUser != null) {
			// 入力された生のパスワードをハッシュ化してデータベースのハッシュと比較
			String encodedPassword = PasswordEncoder.encode(userRequest.getPassword());
			if (existingUser.getPassword().equals(encodedPassword)) {
				return existingUser;
			}
		}

		/*/バッシュ化パスワードの一致を確認②
		if (existingUser != null && passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
			return existingUser;
		}*/

		return null;
	}
}
