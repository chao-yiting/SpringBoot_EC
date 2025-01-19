package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.MConsumptionTax;

public interface MConsumptionTaxRepository extends JpaRepository<MConsumptionTax, Long> {
	// 必要に応じてメソッドを追加
}