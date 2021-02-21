package com.ravinder.singh.composite.product;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.ravinder.singh.api.core.product.Product;
import com.ravinder.singh.api.core.recommendation.Recommendation;
import com.ravinder.singh.api.core.review.Review;
import com.ravinder.singh.composite.product.service.ProductCompositeIntegration;
import com.ravinder.singh.util.exceptions.InvalidInputException;
import com.ravinder.singh.util.exceptions.NotFoundException;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductCompositeServiceApplicationTests {

	private static final int PRODUCT_ID_OK = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 2;
	private static final int PRODUCT_ID_INVALID = 3;

	@Autowired
	private WebTestClient client;

	@MockBean
	private ProductCompositeIntegration compositeIntegration;

	@Before
	public void setUp() {

		when(compositeIntegration.getProduct(PRODUCT_ID_OK))
				.thenReturn(new Product(PRODUCT_ID_OK, "name", 1, "mock-address"));
		when(compositeIntegration.getRecommendations(PRODUCT_ID_OK)).thenReturn(
				singletonList(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address")));
		when(compositeIntegration.getReviews(PRODUCT_ID_OK)).thenReturn(
				singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address")));

		when(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
				.thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

		when(compositeIntegration.getProduct(PRODUCT_ID_INVALID))
				.thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void getProductById() {

		client.get().uri("/product-composite/" + PRODUCT_ID_OK).accept(APPLICATION_JSON).exchange().expectStatus()
				.isOk().expectHeader().contentType(APPLICATION_JSON).expectBody().jsonPath("$.productId")
				.isEqualTo(PRODUCT_ID_OK).jsonPath("$.recommendations.length()").isEqualTo(1)
				.jsonPath("$.reviews.length()").isEqualTo(1);
	}

	@Test
	public void getProductNotFound() {

		client.get().uri("/product-composite/" + PRODUCT_ID_NOT_FOUND).accept(APPLICATION_JSON).exchange()
				.expectStatus().isNotFound().expectHeader().contentType(APPLICATION_JSON).expectBody()
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND).jsonPath("$.message")
				.isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
	}

	@Test
	public void getProductInvalidInput() {

		client.get().uri("/product-composite/" + PRODUCT_ID_INVALID).accept(APPLICATION_JSON).exchange().expectStatus()
				.isEqualTo(UNPROCESSABLE_ENTITY).expectHeader().contentType(APPLICATION_JSON).expectBody()
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID).jsonPath("$.message")
				.isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
	}
}
