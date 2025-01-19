package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;

//CRUD（作成、読み取り、更新、削除）操作を行うためのメソッドが自動的に生成される。
public interface UserRepository extends JpaRepository<User, Long> {
	//指定されたメールアドレスを持つユーザーを検索するメソッド
	User findBymailAddress(String email);
	// パスワードは自動的にハッシュ化されるX
    //User save(User user);
}