package com.company.service;

import com.company.domain.Advertisement;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class AdvertisementService {
	private static final long EXECUTION_DELAY = 25L;
	private ScheduledExecutorService executorService;

	public AdvertisementService() {
		this.executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	}

	public void getAdvertisement(CompletableFuture<Advertisement> completableFuture) {
		this.executorService.schedule(() -> completableFuture.complete(new Advertisement()), EXECUTION_DELAY, TimeUnit.MILLISECONDS);
	}
}
