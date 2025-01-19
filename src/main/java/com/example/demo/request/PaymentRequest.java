package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentRequest {

	//@NotBlank(message = "カード番号を入力してください")
	//@Size(min = 16, max = 16, message = "カード番号は16桁で入力してください")
	private String cardNumber;

	//@NotBlank(message = "カード種類を選択してください")
	private String cardKind;

	//@NotBlank(message = "有効期限（年）を入力してください")
	//@Size(min = 2, max = 2, message = "有効期限（年）は西暦下2桁で入力してください")
	//@Pattern(regexp = "^(0[0-9]|[1-9][0-9])$", message = "有効期限（年）は00から99の間で入力してください") //正規表現今回はなくてもいい
	private String expirationYear;

	//@NotBlank(message = "有効期限（月）を入力してください")
	//@Size(min = 2, max = 2, message = "有効期限（月）は2桁で入力してください")
	private String expirationMonth;//フォームと紐づけして、入力された値を保存する場所

	@NotEmpty(message = "カード名義人を入力してください")
	private String cardHolder;

	@NotBlank(message = "氏名(姓)を入力してください")
	private String firstName;

	@NotBlank(message = "氏名(名)を入力してください")
	private String lastName;

	@NotBlank(message = "都道府県を選択してください")
	private String prefecturesId;

	@NotBlank(message = "郵便番号(前半)を入力してください")
	@Size(min = 3, max = 3, message = "郵便番号の前半3桁で入力してください")
	private String zipCode1;

	@NotBlank(message = "郵便番号(後半)を入力してください")
	@Size(min = 4, max = 4, message = "郵便番号の後半4桁で入力してください")
	private String zipCode2;

	@NotBlank(message = "住所を入力してください")
	private String address;

	//決済システムに使うトークン
	//private int amount; // 支払金額
	//private String currency; // 通貨の種類　日本円なので"jpy"と入力する

	private String stripeToken;
	//private String cardToken;

}
