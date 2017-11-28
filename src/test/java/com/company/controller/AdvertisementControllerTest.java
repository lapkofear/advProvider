package com.company.controller;


import com.company.domain.Advertisement;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Logger;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * To run this test use --add-modules java.xml.bind  compiler option
 */


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AdvertisementControllerTest {
	private static final Logger LOG = Logger.getLogger(AdvertisementControllerTest.class.getName());

	private RestTemplate restTemplate = new RestTemplate();

	private ObjectMapper objectMapper = new ObjectMapper();

	private static WireMockServer wireMockServer;


	@BeforeClass
	public static void setUp() {
		wireMockServer = new WireMockServer(wireMockConfig()
				.port(8081)
				.notifier(new Slf4jNotifier(true)));
		wireMockServer.start();
		configureFor("localhost", wireMockServer.port());
	}

	public void mockStoreResponse(Integer delay) {
		wireMockServer.resetAll();
		try {
			wireMockServer.stubFor(any(urlEqualTo("/advertisementStore"))
					.willReturn(
							aResponse()
									.withFixedDelay(delay)
									.withStatus(HttpStatus.OK.value())
									.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
									.withBody(objectMapper.writeValueAsString(new Advertisement()))));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void successfulRequestTest() {
		mockStoreResponse(100);
		ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8082/advertisement?timeout=5505", String.class);
		LOG.info("===>  response body " + responseEntity.getBody());
		Assert.assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.OK));
		Assert.assertThat(responseEntity.getBody(), IsNull.notNullValue());
	}

	@Test
	public void slowThirdPartyServiceTest() {
		mockStoreResponse(500);

		ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8082/advertisement?timeout=150", String.class);
		LOG.info("===>  response body " + responseEntity.getBody());
		Assert.assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.OK));
		Assert.assertThat(responseEntity.getBody(), IsNull.nullValue());
	}

	@Test
	public void timeoutDueToOurDelayTest() {
		mockStoreResponse(5);     //Third party service answers almost immediately

		ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8082/advertisement?timeout=10", String.class);
		LOG.info("===>  response body " + responseEntity.getBody());
		Assert.assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.OK));
		Assert.assertThat(responseEntity.getBody(), IsNull.nullValue());
	}
}