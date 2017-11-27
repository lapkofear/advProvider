package com.company.controller;


import com.company.domain.Advertisement;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
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
	private ResponseDefinitionBuilder responseMock;



	@BeforeClass
	public static void setUp() {
		wireMockServer = new WireMockServer(wireMockConfig().port(8086));
		wireMockServer.start();
		configureFor("localhost", wireMockServer.port());
	}

	@Before
	public void beforeTest() throws Exception {
		responseMock = aResponse();
		wireMockServer.resetMappings();
		wireMockServer.stubFor(any(urlEqualTo("/advertisementStore"))
				.willReturn(
						responseMock
								.withStatus(HttpStatus.OK.value())
								.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
								.withBody(objectMapper.writeValueAsString(new Advertisement()))));
	}

	@Test
	public void successfulRequestTest() {
		responseMock.withFixedDelay(100);
		ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8082/advertisement?timeout=250", String.class);
		LOG.info("===>  response body " + responseEntity.getBody());
		Assert.assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.OK));
		Assert.assertThat(responseEntity.getBody(), IsNull.notNullValue());
	}

	@Test
	public void slowThirdPartyServiceTest() {
		responseMock.withFixedDelay(500);

		ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8082/advertisement?timeout=150", String.class);
		LOG.info("===>  response body " + responseEntity.getBody());
		Assert.assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.OK));
		Assert.assertThat(responseEntity.getBody(), IsNull.notNullValue());
	}

	@Test
	public void timeoutDueToOurDelayTest() {
		responseMock.withFixedDelay(5);     //Third party service answers almost immediately

		ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8082/advertisement?timeout=10", String.class);
		LOG.info("===>  response body " + responseEntity.getBody());
		Assert.assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.OK));
		Assert.assertThat(responseEntity.getBody(), IsNull.nullValue());
	}
}