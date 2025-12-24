package com.accenture.api;

import com.accenture.exception.ResourceNotFoundException;
import com.accenture.model.dto.*;
import com.accenture.service.BranchService;
import com.accenture.service.FranchiseService;
import com.accenture.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(FranchiseController.class)
class FranchiseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FranchiseService franchiseService;

    @MockitoBean
    private BranchService branchService;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("Should return all franchises")
    void shouldReturnAllFranchises() {
        // Arrange
        FranchiseResponse f1 = FranchiseResponse.builder().id(1L).name("Franchise 1").build();
        FranchiseResponse f2 = FranchiseResponse.builder().id(2L).name("Franchise 2").build();

        when(franchiseService.getAllFranchises()).thenReturn(Flux.just(f1, f2));

        // Act & Assert
        webTestClient.get()
                .uri("/v1/franchises")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBodyList(FranchiseResponse.class)
                .hasSize(2)
                .contains(f1, f2);
    }

    @Test
    @DisplayName("Should create franchise when request is valid")
    void shouldCreateFranchiseWhenRequestIsValid() throws Exception {
        // Arrange
        FranchiseRequest request = FranchiseRequest.builder().name("Accenture").build();
        FranchiseResponse response = FranchiseResponse.builder().id(1L).name("Accenture").build();

        when(franchiseService.createFranchise(any(FranchiseRequest.class))).thenReturn(Mono.just(response));

        // Act & Assert
        webTestClient.post()
                .uri("/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Accenture");
    }

    @Test
    @DisplayName("Should return 400 when create franchise has invalid name")
    void shouldReturnBadRequestWhenFranchiseNameIsEmpty() {
        // Arrange
        FranchiseRequest request = FranchiseRequest.builder().name("").build();

        // Act & Assert
        webTestClient.post()
                .uri("/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Validation Error")
                .jsonPath("$.detail").value(detail -> ((String) detail).contains("name"));
    }

    @Test
    @DisplayName("Should add branch when franchise exists")
    void shouldAddBranchWhenFranchiseExists() {
        // Arrange
        Long franchiseId = 1L;
        BranchRequest request = BranchRequest.builder().name("Branch North").build();
        BranchResponse response = BranchResponse.builder().id(1L).name("Branch North").franchise_id(franchiseId)
                .build();

        when(branchService.addBranchToFranchise(eq(franchiseId), any(BranchRequest.class)))
                .thenReturn(Mono.just(response));

        // Act & Assert
        webTestClient.post()
                .uri("/v1/franchises/{franchiseId}/branches", franchiseId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Branch North")
                .jsonPath("$.franchise_id").isEqualTo(1);
    }

    @Test
    @DisplayName("Should get top products for a franchise")
    void shouldReturnTopProductsWhenFranchiseExists() {
        // Arrange
        Long franchiseId = 1L;
        SpecificProductResponse product1 = SpecificProductResponse.builder()
                .product_id(10L).product_name("Laptop").stock(50).branch_name("Branch A").build();

        when(productService.getTopProductsByFranchise(franchiseId)).thenReturn(Flux.fromIterable(List.of(product1)));

        // Act & Assert
        webTestClient.get()
                .uri("/v1/franchises/{franchiseId}/top-products", franchiseId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].product_name").isEqualTo("Laptop")
                .jsonPath("$[0].branch_name").isEqualTo("Branch A");
    }

    @Test
    @DisplayName("Should return 404 when franchise is not found for top products")
    void shouldReturnNotFoundWhenFranchiseDoesNotExist() {
        // Arrange
        Long franchiseId = 99L;
        when(productService.getTopProductsByFranchise(franchiseId))
                .thenReturn(Flux.error(new ResourceNotFoundException("Franchise not found")));

        // Act & Assert
        webTestClient.get()
                .uri("/v1/franchises/{franchiseId}/top-products", franchiseId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Resource Not Found")
                .jsonPath("$.detail").isEqualTo("Franchise not found");
    }

    @Test
    @DisplayName("Should update franchise name successfully")
    void shouldUpdateFranchiseWhenIdExists() {
        // Arrange
        Long id = 1L;
        FranchiseRequest request = FranchiseRequest.builder().name("Updated Name").build();
        FranchiseResponse response = FranchiseResponse.builder().id(id).name("Updated Name").build();

        when(franchiseService.updateFranchise(eq(id), any(FranchiseRequest.class))).thenReturn(Mono.just(response));

        // Act & Assert
        webTestClient.put()
                .uri("/v1/franchises/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("Should handle generic exceptions with 500 status")
    void shouldReturnInternalServerErrorWhenUnexpectedErrorOccurs() {
        // Arrange
        when(franchiseService.createFranchise(any())).thenReturn(Mono.error(new Exception("Internal Fail")));

        // Act & Assert
        webTestClient.post()
                .uri("/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(FranchiseRequest.builder().name("Failure").build())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Internal Server Error");
    }
}
