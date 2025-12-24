package com.accenture.service;

import com.accenture.model.dto.ProductNameRequest;
import com.accenture.model.dto.ProductRequest;
import com.accenture.model.dto.ProductResponse;
import com.accenture.model.dto.ProductStockRequest;
import com.accenture.model.dto.SpecificProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<ProductResponse> addProductToBranch(Long branchId, ProductRequest request);

    Mono<Void> deleteProduct(Long productId);

    Mono<ProductResponse> updateStock(Long productId, ProductStockRequest request);

    Mono<ProductResponse> updateProductName(Long productId, ProductNameRequest request);

    Flux<SpecificProductResponse> getTopProductsByFranchise(Long franchiseId);
}
