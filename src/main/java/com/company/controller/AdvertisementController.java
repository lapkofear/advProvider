package com.company.controller;

import com.company.domain.Advertisement;
import com.company.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@RestController
public class AdvertisementController {
	private static final long DEFAULT_TIMEOUT_MILLIS = 50;

	private final AdvertisementService advertisementService;

	@Autowired
	public AdvertisementController(AdvertisementService advertisementService) {
		this.advertisementService = advertisementService;
	}

	@GetMapping(value = "/advertisement", produces = MediaType.APPLICATION_JSON_VALUE)
	public CompletableFuture<Advertisement> advertisement(@RequestParam(value = "timeout", required = false) String timeout) {

		CompletableFuture<Advertisement> completableFuture = new CompletableFuture<>();
		completableFuture.completeOnTimeout(null, getTimeout(timeout), TimeUnit.MILLISECONDS);
		this.advertisementService.getAdvertisement(completableFuture);

		return completableFuture;
	}

	private long getTimeout(String customTimeout) {
		return customTimeout != null ? Long.parseLong(customTimeout) : DEFAULT_TIMEOUT_MILLIS;
	}
}
