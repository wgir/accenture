package com.accenture.api;

import com.accenture.exception.ResourceNotFoundException;
import com.accenture.model.dto.ProductNameRequest;
import com.accenture.model.dto.ProductResponse;
import com.accenture.model.dto.ProductStockRequest;
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

@WebFluxTest(ProductController.class)
class ProductControllerTest {

        @Autowired
        private WebTestClient webTestClient;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private ProductService productService;

        @Test
        @DisplayName("Should delete product when ID exists")
        void shouldDeleteProductWhenIdExists() {
                // Arrange
                Long productId = 1L;
                when(productService.deleteProduct(productId)).thenReturn(Mono.empty());

                // Act & Assert
                webTestClient.delete()
                                .uri("/v1/products/{productId}", productId)
                                .exchange()
                                .expectStatus().isNoContent();
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent product")
        void shouldReturnNotFoundWhenDeletingNonExistentProduct() {
                // Arrange
                Long productId = 99L;
                when(productService.deleteProduct(productId))
                                .thenReturn(Mono.error(new ResourceNotFoundException("Product not found")));

                // Act & Assert
                webTestClient.delete()
                                .uri("/v1/products/{productId}", productId)
                                .exchange()
                                .expectStatus().isNotFound()
                                .expectBody()
                                .jsonPath("$.title").isEqualTo("Resource Not Found")
                                .jsonPath("$.detail").isEqualTo("Product not found");
        }

        @Test
        @DisplayName("Should update product stock when request is valid")
        void shouldUpdateStockWhenRequestIsValid() throws Exception {
                // Arrange
                Long productId = 1L;
                ProductStockRequest request = ProductStockRequest.builder().stock(50).build();
                ProductResponse response = ProductResponse.builder()
                                .id(productId)
                                .name("Smartphone")
                                .stock(50)
                                .branch_id(10L)
                                .build();

                when(productService.updateStock(eq(productId), any(ProductStockRequest.class)))
                                .thenReturn(Mono.just(response));

                // Act & Assert
                webTestClient.patch()
                                .uri("/v1/products/{productId}/stock", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(objectMapper.writeValueAsString(request))
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody()
                                .jsonPath("$.id").isEqualTo(productId)
                                .jsonPath("$.stock").isEqualTo(50);
        }

        @Test
        @DisplayName("Should return 400 when updating stock with negative value")
        void shouldReturnBadRequestWhenStockIsNegative() {
                // Arrange
                Long productId = 1L;
                ProductStockRequest request = ProductStockRequest.builder().stock(-10).build();

                // Act & Assert
                webTestClient.patch()
                                .uri("/v1/products/{productId}/stock", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(request)
                                .exchange()
                                .expectStatus().isBadRequest()
                                .expectBody()
                                .jsonPath("$.title").isEqualTo("Validation Error")
                                .jsonPath("$.detail").value(detail -> ((String) detail).contains("stock"));
        }

        @Test
        @DisplayName("Should update product name when request is valid")
        void shouldUpdateProductNameWhenRequestIsValid() throws Exception {
                // Arrange
                Long productId = 1L;
                ProductNameRequest request = ProductNameRequest.builder().name("New Tablet Name").build();
                ProductResponse response = ProductResponse.builder()
                                .id(productId)
                                .name("New Tablet Name")
                                .stock(20)
                                .branch_id(10L)
                                .build();

                when(productService.updateProductName(eq(productId), any(ProductNameRequest.class)))
                                .thenReturn(Mono.just(response));

                // Act & Assert
                webTestClient.patch()
                                .uri("/v1/products/{productId}/name", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(objectMapper.writeValueAsString(request))
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody()
                                .jsonPath("$.id").isEqualTo(productId)
                                .jsonPath("$.name").isEqualTo("New Tablet Name");
        }

        @Test
        @DisplayName("Should return 400 when updating name with blank value")
        void shouldReturnBadRequestWhenNameIsBlank() {
                // Arrange
                Long productId = 1L;
                ProductNameRequest request = ProductNameRequest.builder().name("").build();

                // Act & Assert
                webTestClient.patch()
                                .uri("/v1/products/{productId}/name", productId)
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
                Long productId = 1L;
                when(productService.deleteProduct(productId))
                                .thenReturn(Mono.error(new Exception("Database connection failure")));

                // Act & Assert
                webTestClient.delete()
                                .uri("/v1/products/{productId}", productId)
                                .exchange()
                                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                                .expectBody()
                                .jsonPath("$.title").isEqualTo("Internal Server Error");
        }
}
