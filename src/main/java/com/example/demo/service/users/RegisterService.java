package com.example.demo.service.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.RegisterRequest;
import com.example.demo.util.PasswordEncoder;

@Service //ビジネスロジックを提供する
public class RegisterService {

	@Autowired //依存性注入
	private UserRepository userRepository; //UserRepositoryクラスを使えるようにする

	/*	@Autowired //ハッシュ化②
		private BCryptPasswordEncoder passwordEncoder;// BCryptPasswordEncoderのインスタンスを注入
		*/

	/*public User registerUser(User user) {
		// パスワードを暗号化①
		String encryptedPassword = PasswordEncoder.encode(user.getPassword());
		user.setPassword(encryptedPassword);
	*/
	/*/ パスワードをBCryptでハッシュ化②
	user.setPassword(passwordEncoder.encode(user.getPassword()));
	*/
	/*
			// ユーザー情報をデータベースに保存
			return userRepository.save(user); // 保存したユーザーエンティティを返す
		}*/

	public void saveRegisterUser(RegisterRequest userRequest) {

		// パスワードを暗号化①
		String encryptedPassword = PasswordEncoder.encode(userRequest.getPassword());

		// Userオブジェクトを新規作成
		User user = new User();
		user.setUserName(userRequest.getUserName());
		user.setMailAddress(userRequest.getMailAddress());
		user.setPassword(encryptedPassword);

		// ユーザー情報をデータベースに保存
		userRepository.save(user);
	}

}
