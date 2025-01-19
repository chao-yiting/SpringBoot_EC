package com.example.demo.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartRequest {

	@NotNull(message = "数量を入力してください")
	@Min(value = 1, message = "数量は1以上で入力してください")
	private Integer quantity;

	private Long productId; // productIdフィールドを追加
}
