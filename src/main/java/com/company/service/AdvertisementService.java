package com.company.service;

import com.company.domain.Advertisement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AdvertisementService {
	private static final long EXECUTION_DELAY = 25L;

	@Value("${advertisement.store.url}")
	private String advertisementStoreUrl;
	private RestTemplate restTemplate = new RestTemplate();

	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());


	public void getAdvertisement(CompletableFuture<Advertisement> completableFuture) {
		this.executorService.schedule(getAdvertisementFromStoreTask(completableFuture), EXECUTION_DELAY, TimeUnit.MILLISECONDS);
	}

	private Runnable getAdvertisementFromStoreTask(CompletableFuture<Advertisement> completableFuture) {
		return () -> {
			ResponseEntity<Advertisement> entity = restTemplate.getForEntity(advertisementStoreUrl, Advertisement.class);
			completableFuture.complete(entity.getStatusCode() == HttpStatus.OK ? entity.getBody() : null);
		};
	}
}
