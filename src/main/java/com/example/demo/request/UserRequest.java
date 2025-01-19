package com.example.demo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UserRequest {
	@Min(value = 0, message = "ユーザー権限を選択してください")
	@Max(value = 1, message = "ユーザー権限を選択してください")
	private int userAuthority;
}
