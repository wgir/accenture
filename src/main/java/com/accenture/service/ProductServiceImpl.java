package com.accenture.service;

import com.accenture.exception.ConflictException;
import com.accenture.exception.ResourceNotFoundException;
import com.accenture.model.Product;
import com.accenture.model.dto.ProductNameRequest;
import com.accenture.model.dto.ProductRequest;
import com.accenture.model.dto.ProductResponse;
import com.accenture.model.dto.ProductStockRequest;
import com.accenture.model.dto.SpecificProductResponse;
import com.accenture.repository.BranchRepository;
import com.accenture.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    @Transactional
    public Mono<ProductResponse> addProductToBranch(Long branchId, ProductRequest request) {
        log.info("Adding product '{}' to branch id {}", request.getName(), branchId);
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Branch not found")))
                .flatMap(branch -> productRepository.existsByNameIgnoreCaseAndBranchId(request.getName(), branchId)
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new ConflictException("Product name already exists in this branch"));
                            }
                            return Mono.defer(() -> productRepository.save(Product.builder()
                                    .name(request.getName())
                                    .stock(request.getStock())
                                    .branchId(branch.getId())
                                    .build()));
                        }))
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public Mono<Void> deleteProduct(Long productId) {
        log.info("Deleting product id {}", productId);
        return productRepository.existsById(productId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResourceNotFoundException("Product not found"));
                    }
                    return productRepository.deleteById(productId);
                });
    }

    @Override
    @Transactional
    public Mono<ProductResponse> updateStock(Long productId, ProductStockRequest request) {
        log.info("Updating stock for product id {}: {}", productId, request.getStock());
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product not found")))
                .flatMap(product -> {
                    product.setStock(request.getStock());
                    return productRepository.save(product);
                })
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public Mono<ProductResponse> updateProductName(Long productId, ProductNameRequest request) {
        log.info("Updating name for product id {}: {}", productId, request.getName());
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product not found")))
                .flatMap(product -> {
                    if (product.getName().equalsIgnoreCase(request.getName())) {
                        return Mono.just(product);
                    }
                    return productRepository.existsByNameIgnoreCaseAndBranchId(request.getName(), product.getBranchId())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(
                                            new ConflictException("Product name already exists in this branch"));
                                }
                                product.setName(request.getName());
                                return productRepository.save(product);
                            });
                })
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SpecificProductResponse> getTopProductsByFranchise(Long franchiseId) {
        log.info("Getting top products for franchise id {}", franchiseId);
        String query = "SELECT p.id as product_id, p.name as product_name, p.stock, b.name as branch_name " +
                "FROM products p " +
                "JOIN branches b ON p.branch_id = b.id " +
                "WHERE b.franchise_id = :franchiseId " +
                "AND p.stock = (SELECT MAX(p2.stock) FROM products p2 WHERE p2.branch_id = p.branch_id)";

        return entityTemplate.getDatabaseClient().sql(query)
                .bind("franchiseId", franchiseId)
                .map((row, metadata) -> SpecificProductResponse.builder()
                        .product_id(row.get("product_id", Long.class))
                        .product_name(row.get("product_name", String.class))
                        .stock(row.get("stock", Integer.class))
                        .branch_name(row.get("branch_name", String.class))
                        .build())
                .all();
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .branch_id(product.getBranchId())
                .build();
    }
}
