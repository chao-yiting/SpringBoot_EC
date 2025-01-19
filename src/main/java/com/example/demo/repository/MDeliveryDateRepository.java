package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.MDeliveryDate;

public interface MDeliveryDateRepository extends JpaRepository<MDeliveryDate, Long> {
    // 必要に応じてメソッドを追加
}