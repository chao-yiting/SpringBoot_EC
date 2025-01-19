package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.MDeliveryDate;
import com.example.demo.entity.MPostage;
import com.example.demo.entity.MPrefectures;
import com.example.demo.repository.MDeliveryDateRepository;
import com.example.demo.repository.MPostageRepository;
import com.example.demo.repository.MPrefecturesRepository;

@Service
public class ShippingInfoService {
	@Autowired
	private MPrefecturesRepository prefecturesRepository;

	@Autowired
	private MPostageRepository postageRepository;

	@Autowired
	private MDeliveryDateRepository deliveryDateRepository;

	public Map<String, Integer> getShippingInfoByPrefectureId(Long prefectureId) {
		Optional<MPrefectures> prefectureOptional = prefecturesRepository.findById(prefectureId);

		if (prefectureOptional.isPresent()) {
			MPrefectures prefecture = prefectureOptional.get();

			MPostage postage = postageRepository.findById(prefecture.getPostageId()).orElseThrow();
			MDeliveryDate deliveryDate = deliveryDateRepository.findById(prefecture.getDeliveryDateId())
					.orElseThrow();

			Map<String, Integer> shippingInfo = new HashMap<>();
			shippingInfo.put("postage", postage.getPostage());
			shippingInfo.put("deliveryDate", deliveryDate.getDeliveryDate());

			return shippingInfo;
		} else {
			return null;
		}
	}
}
