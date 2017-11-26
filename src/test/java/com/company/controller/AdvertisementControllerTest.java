package com.company.controller;


import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AdvertisementControllerTest {
	private static final Logger LOG = Logger.getLogger(AdvertisementControllerTest.class.getName());

	private RestTemplate restTemplate = new RestTemplate();

	@Test
	public void successfulRequestTest() throws InterruptedException {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8082/advertisement?timeout=2000", String.class);
		LOG.info("===>  response body " + responseEntity.getBody());
		Assert.assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.OK));
		Assert.assertThat(responseEntity.getBody(), IsNull.notNullValue());
	}

	@Test
	public void timeoutTest() throws InterruptedException {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8082/advertisement?timeout=10", String.class);
		LOG.info("===>  response body " + responseEntity.getBody());
		Assert.assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.OK));
		Assert.assertThat(responseEntity.getBody(), IsNull.nullValue());
		}
	}