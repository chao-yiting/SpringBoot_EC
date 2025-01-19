package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductsRequest {

	@NotBlank(message = "商品名を入力してください")
	private String name;

	@NotBlank(message = "カテゴリーを選択してください")
	private String categoryId;

	@NotBlank(message = "商品説明を入力してください")
	private String description;

	@NotNull(message = "商品価格を入力してください")
	@Positive(message = "商品価格は正の値で入力してください")
	private Integer price;

	@NotNull(message = "在庫数を入力してください")
	@PositiveOrZero(message = "在庫数は0以上の値で入力してください")
	private Integer stock;

//	@NotNull
//	public String id;
}
