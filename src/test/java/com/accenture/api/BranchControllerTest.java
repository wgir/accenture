package com.accenture.api;

import com.accenture.model.dto.*;
import com.accenture.service.BranchService;
import com.accenture.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(BranchController.class)
class BranchControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private BranchService branchService;

    @Test
    @DisplayName("Should add product to branch when request is valid")
    void shouldAddProductWhenRequestIsValid() throws Exception {
        // Arrange
        Long branchId = 1L;
        ProductRequest request = ProductRequest.builder()
                .name("Laptop")
                .stock(10)
                .build();
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .stock(10)
                .branch_id(branchId)
                .build();

        when(productService.addProductToBranch(eq(branchId), any(ProductRequest.class)))
                .thenReturn(Mono.just(response));

        // Act & Assert
        webTestClient.post()
                .uri("/v1/branches/{branchId}/products", branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Laptop")
                .jsonPath("$.stock").isEqualTo(10)
                .jsonPath("$.branch_id").isEqualTo(branchId);
    }

    @Test
    @DisplayName("Should return 400 when adding product with invalid data")
    void shouldReturnBadRequestWhenProductDataIsInvalid() {
        // Arrange
        Long branchId = 1L;
        ProductRequest request = ProductRequest.builder()
                .name("") // Invalid name
                .stock(-5) // Invalid stock
                .build();

        // Act & Assert
        webTestClient.post()
                .uri("/v1/branches/{branchId}/products", branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Validation Error")
                .jsonPath("$.detail").value(detail -> {
                    String d = (String) detail;
                    org.junit.jupiter.api.Assertions.assertTrue(d.contains("name") || d.contains("stock"));
                });
    }

    @Test
    @DisplayName("Should return 404 when adding product to non-existent branch")
    void shouldReturnNotFoundWhenBranchDoesNotExistForProduct() {
        // Arrange
        Long branchId = 99L;
        ProductRequest request = ProductRequest.builder()
                .name("Laptop")
                .stock(10)
                .build();

        when(productService.addProductToBranch(eq(branchId), any(ProductRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Branch not found")));

        // Act & Assert
        webTestClient.post()
                .uri("/v1/branches/{branchId}/products", branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Resource Not Found")
                .jsonPath("$.detail").isEqualTo("Branch not found");
    }

    @Test
    @DisplayName("Should update branch name successfully")
    void shouldUpdateBranchWhenIdExists() {
        // Arrange
        Long branchId = 1L;
        BranchRequest request = BranchRequest.builder()
                .name("New Branch Name")
                .build();
        BranchResponse response = BranchResponse.builder()
                .id(branchId)
                .name("New Branch Name")
                .franchise_id(10L)
                .build();

        when(branchService.updateBranch(eq(branchId), any(BranchRequest.class)))
                .thenReturn(Mono.just(response));

        // Act & Assert
        webTestClient.put()
                .uri("/v1/branches/{id}", branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(branchId)
                .jsonPath("$.name").isEqualTo("New Branch Name");
    }

    @Test
    @DisplayName("Should return 400 when updating branch with empty name")
    void shouldReturnBadRequestWhenBranchNameIsEmpty() {
        // Arrange
        Long branchId = 1L;
        BranchRequest request = BranchRequest.builder()
                .name("")
                .build();

        // Act & Assert
        webTestClient.put()
                .uri("/v1/branches/{id}", branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Validation Error");
    }

    @Test
    @DisplayName("Should handle generic exception with 500 status")
    void shouldReturnInternalServerErrorWhenUnexpectedErrorOccurs() {
        // Arrange
        Long branchId = 1L;
        BranchRequest request = BranchRequest.builder()
                .name("Branch")
                .build();

        when(branchService.updateBranch(any(), any()))
                .thenReturn(Mono.error(new Exception("Database down")));

        // Act & Assert
        webTestClient.put()
                .uri("/v1/branches/{id}", branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Internal Server Error");
    }
}
