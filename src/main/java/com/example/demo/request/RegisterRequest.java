package com.example.demo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

	@NotBlank(message = "ユーザー名を入力してください")
	private String userName;

	@NotBlank(message = "メールアドレスを入力してください")
	@Email(message = "正しいメールアドレスの形式で入力してください")
	//@UniqueEmail(message = "このメールアドレスは既に登録されています")
	private String mailAddress;

	@NotBlank(message = "パスワードを入力してください")
	@Size(min = 6, message = "パスワードは少なくとも6文字以上で入力してください")
	private String password;

}
