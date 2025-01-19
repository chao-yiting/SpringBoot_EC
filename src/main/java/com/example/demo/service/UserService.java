package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service //ビジネスロジックを提供する
public class UserService {

	/*@Autowired //依存性注入
	private UserRepository userRepository;
	// ユーザーをIDで検索するメソッド
	public User getUserById(Long userId) {
		// データベースなどからユーザー情報を取得するロジックを実装する
		return userRepository.findById(userId).orElse(null);
		//return null; // ダミーデータを返す
	}*/

	/*	@Autowired //依存性注入
		private UserRepository userRepository; //UserRepositoryクラスを使えるようにする
	
		public User registerUser(User user) {
			// ユーザー情報のバリデーションなどを行う
			// ユーザー情報をデータベースに保存
			return userRepository.save(user); // 保存したユーザーエンティティを返す
		}*/

	/*/ユーザー認証
	//渡されたメールアドレスとパスワードを使用してユーザー認証を行う
	public User authenticateUser(User user) {
		// ユーザーの認証ロジック
		// userRepository.findBymailAddress()を使用してユーザーを取得し、パスワードの一致を確認
		User existingUser = userRepository.findBymailAddress(user.getMailAddress());
		if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
			return existingUser;
		}
		return null;
	}*/

	// 他のユーザー関連のメソッドを追加できる

}
