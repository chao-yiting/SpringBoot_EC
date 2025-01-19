package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.MPrefectures;

public interface MPrefecturesRepository extends JpaRepository<MPrefectures, Long> {
	// 必要に応じてメソッドを追加
}