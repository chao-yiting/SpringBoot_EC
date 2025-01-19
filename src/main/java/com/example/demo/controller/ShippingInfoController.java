package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ShippingInfoService;

@RestController
@RequestMapping("/shipping")
public class ShippingInfoController {

	@Autowired
	private ShippingInfoService shippingInfoService;

	@GetMapping("/info")
	public ResponseEntity<Map<String, Integer>> getShippingInfo(@RequestParam Long prefectureId) {
		Map<String, Integer> shippingInfo = shippingInfoService.getShippingInfoByPrefectureId(prefectureId);
		return ResponseEntity.ok(shippingInfo);
	}
}
