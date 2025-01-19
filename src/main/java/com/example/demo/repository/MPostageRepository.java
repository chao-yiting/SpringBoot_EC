package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.MPostage;

public interface MPostageRepository extends JpaRepository<MPostage, Long> {
    // 必要に応じてメソッドを追加
}