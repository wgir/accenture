package com.accenture.api;

import com.accenture.model.dto.ProductNameRequest;
import com.accenture.model.dto.ProductResponse;
import com.accenture.model.dto.ProductStockRequest;
import com.accenture.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @PatchMapping("/{id}/stock")
    public Mono<ProductResponse> updateStock(@PathVariable Long id, @Valid @RequestBody ProductStockRequest request) {
        return productService.updateStock(id, request);
    }

    @PatchMapping("/{id}/name")
    public Mono<ProductResponse> updateName(@PathVariable Long id, @Valid @RequestBody ProductNameRequest request) {
        return productService.updateProductName(id, request);
    }
}
