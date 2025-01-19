package com.example.demo.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "mail_address")
	private String mailAddress;

	@Column(name = "password"/*, nullable = false*/)
	private String password;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "user_authority")
	private int userAuthority;

	@Column(name = "user_status")
	private int userStatus;

	@CreationTimestamp
	@Column(name = "created_at")
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Timestamp updatedAt;

	// セッション情報を格納するためのフィールド
	@Transient // データベースに保存しない
	private boolean loggedIn;

	/*/ getId()メソッド
	public Long getId() {
		return id;
	}*/
}
