package br.com.erudio.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.erudio.configs.TestsConfigs;
import br.com.erudio.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerYamlTest extends AbstractIntegrationTest {

	private static YMLMapper objectMapper;
	private static TokenVO tokenVO;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new YMLMapper();
	}

	@Test
	@Order(1)
	public void testSignin() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

		tokenVO = given()
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
										TestsConfigs.CONTENT_TYPE_YML, 
										ContentType.TEXT)))
				.accept(TestsConfigs.CONTENT_TYPE_YML)
				.basePath("/auth/signin")
					.port(TestsConfigs.SERVER_PORT)
					.contentType(TestsConfigs.CONTENT_TYPE_YML)
				.body(user, objectMapper)
					.when()
				.post()
					.then()
						.statusCode(200).extract()
				.body().as(TokenVO.class, objectMapper);

		assertNotNull(tokenVO.getAccessToken());
		assertNotNull(tokenVO.getRefreshToken());
	}

	@Test
	@Order(2)
	public void testRefresh() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

		var newTokenVO = given()
						.config(
								RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
									.encodeContentTypeAs(
											TestsConfigs.CONTENT_TYPE_YML, 
											ContentType.TEXT)))
						.accept(TestsConfigs.CONTENT_TYPE_YML)
						.basePath("/auth/refresh")
						.port(TestsConfigs.SERVER_PORT)
						.contentType(TestsConfigs.CONTENT_TYPE_YML)
							.pathParam("username", tokenVO.getUsername())
							.header(TestsConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
						.when().put("{username}").then().statusCode(200).extract()
						.body().as(TokenVO.class, objectMapper);

		assertNotNull(newTokenVO.getAccessToken());
		assertNotNull(newTokenVO.getRefreshToken());
	}

}
