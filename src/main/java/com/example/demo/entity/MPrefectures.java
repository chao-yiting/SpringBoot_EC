package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "m_prefectures")

public class MPrefectures {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "postage_id")
	private Long postageId;

	@Column(name = "delivery_date_id")
	private Long deliveryDateId;

	@Column(name = "prefecture")
	private String prefecture;
}
