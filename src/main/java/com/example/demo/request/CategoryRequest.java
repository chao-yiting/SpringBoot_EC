package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

	@NotBlank(message = "カテゴリー名を入力してください")
	private String categoryName;
}
