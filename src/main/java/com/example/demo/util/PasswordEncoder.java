package com.example.demo.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordEncoder {

	//Base64エンコーディングを使用してハッシュを文字列に変換
	public static String encode(String password) {
		try {
			//ハッシュ化アルゴリズムをSHA-256として初期化
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			//パスワード文字列をバイト配列に変換し、バイト配列に対してハッシュを計算
			byte[] hashBytes = md.digest(password.getBytes());
			// ハッシュされたバイト配列をBase64エンコードして文字列に変換
			return Base64.getEncoder().encodeToString(hashBytes);
		} catch (NoSuchAlgorithmException e) {
			// 例外が発生した場合、エラーメッセージを表示してランタイム例外をスロー
			e.printStackTrace();
			throw new RuntimeException("Password encoding failed.");
		}
	}
}
