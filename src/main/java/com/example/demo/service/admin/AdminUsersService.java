package com.example.demo.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.UserRequest;

@Service
public class AdminUsersService {

	@Autowired
	private UserRepository userRepository;

	//すべてのユーザーを取得し、コントローラに返す
	public List<User> getAllUsers() { //すべてのユーザーをデータベースから取得する
		return userRepository.findAll(); //データベース内のすべてのレコードを抽出してリストとして返す
	}

	//ユーザー権限変更
	public void updateUserAuthority(Long userId, UserRequest userRequest) {
		User user = userRepository.findById(userId).orElse(null);
		if (user != null) {
			user.setUserAuthority(userRequest.getUserAuthority());
			userRepository.save(user); // ユーザーの権限を更新
		}
	}

	// ユーザーをIDで削除するメソッド
	public void deleteUser(Long userId) {
		// 指定したIDのユーザーを削除
		userRepository.deleteById(userId);
	}
}
