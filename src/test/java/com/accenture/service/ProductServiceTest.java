package com.accenture.service;

import com.accenture.model.Branch;
import com.accenture.model.Product;
import com.accenture.model.dto.ProductNameRequest;
import com.accenture.model.dto.ProductRequest;
import com.accenture.model.dto.ProductStockRequest;
import com.accenture.repository.BranchRepository;
import com.accenture.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.BiFunction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

        @Mock
        private ProductRepository productRepository;

        @Mock
        private BranchRepository branchRepository;

        @Mock
        private R2dbcEntityTemplate entityTemplate;

        @InjectMocks
        private ProductServiceImpl productService;

        @Test
        void addProductToBranch_Success() {
                Long branchId = 1L;
                ProductRequest request = ProductRequest.builder().name("New Product").stock(10).build();
                Branch branch = Branch.builder().id(branchId).name("Branch A").build();
                Product savedProduct = Product.builder().id(10L).name("New Product").stock(10).branchId(branchId)
                                .build();

                when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
                when(entityTemplate.insert(any(Product.class))).thenReturn(Mono.just(savedProduct));

                StepVerifier.create(productService.addProductToBranch(branchId, request))
                                .expectNextMatches(response -> response.getId().equals(10L)
                                                && response.getName().equals("New Product"))
                                .verifyComplete();
        }

        @Test
        void deleteProduct_Success() {
                Long productId = 1L;
                when(productRepository.existsById(productId)).thenReturn(Mono.just(true));
                when(productRepository.deleteById(productId)).thenReturn(Mono.empty());

                StepVerifier.create(productService.deleteProduct(productId))
                                .verifyComplete();
        }

        @Test
        void updateStock_Success() {
                Long productId = 1L;
                ProductStockRequest request = ProductStockRequest.builder().stock(50).build();
                Product product = Product.builder().id(productId).name("Product A").stock(10).branchId(2L).build();
                Product updatedProduct = Product.builder().id(productId).name("Product A").stock(50).branchId(2L)
                                .build();

                when(productRepository.findById(productId)).thenReturn(Mono.just(product));
                when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

                StepVerifier.create(productService.updateStock(productId, request))
                                .expectNextMatches(response -> response.getStock() == 50)
                                .verifyComplete();
        }

        @Test
        void getTopProductsByFranchise_Success() {
                DatabaseClient databaseClient = mock(DatabaseClient.class);
                DatabaseClient.GenericExecuteSpec executeSpec = mock(DatabaseClient.GenericExecuteSpec.class);

                when(entityTemplate.getDatabaseClient()).thenReturn(databaseClient);
                when(databaseClient.sql(anyString())).thenReturn(executeSpec);
                when(executeSpec.bind(anyString(), any())).thenReturn(executeSpec);

                // Mocking the map part is complex because it's a BiFunction
                // We can use thenAnswer if needed, but for simplicity in unit tests we might
                // just mock the final Flu

                // Let's use a simpler approach for mocking DatabaseClient chain
                RowsFetchSpec<com.accenture.model.dto.SpecificProductResponse> rowsFetchSpec = mock(
                                RowsFetchSpec.class);
                when(executeSpec.map(any(BiFunction.class))).thenReturn(rowsFetchSpec);

                com.accenture.model.dto.SpecificProductResponse responseDto = com.accenture.model.dto.SpecificProductResponse
                                .builder()
                                .product_id(1L)
                                .product_name("Top Product")
                                .stock(100)
                                .branch_name("Branch A")
                                .build();

                when(rowsFetchSpec.all()).thenReturn(Flux.just(responseDto));

                StepVerifier.create(productService.getTopProductsByFranchise(1L))
                                .expectNextMatches(response -> response.getProduct_id().equals(1L) &&
                                                response.getProduct_name().equals("Top Product") &&
                                                response.getBranch_name().equals("Branch A") &&
                                                response.getStock() == 100)
                                .verifyComplete();
        }

        @Test
        void updateProductName_Success() {
                Product product = Product.builder().id(1L).name("Old Name").stock(10).branchId(1L).build();
                Product updatedProduct = Product.builder().id(1L).name("New Name").stock(10).branchId(1L).build();

                when(productRepository.findById(1L)).thenReturn(Mono.just(product));
                when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

                StepVerifier.create(productService.updateProductName(1L,
                                ProductNameRequest.builder().name("New Name").build()))
                                .expectNextMatches(response -> response.getName().equals("New Name"))
                                .verifyComplete();
        }
}
