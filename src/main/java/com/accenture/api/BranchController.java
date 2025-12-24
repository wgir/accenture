package com.accenture.api;

import com.accenture.model.dto.BranchRequest;
import com.accenture.model.dto.BranchResponse;
import com.accenture.model.dto.ProductRequest;
import com.accenture.model.dto.ProductResponse;
import com.accenture.service.BranchService;
import com.accenture.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;
    private final ProductService productService;

    @PutMapping("/{id}")
    public Mono<BranchResponse> updateBranch(@PathVariable Long id, @Valid @RequestBody BranchRequest request) {
        return branchService.updateBranch(id, request);
    }

    @PostMapping("/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductResponse> addProduct(@PathVariable Long branchId, @Valid @RequestBody ProductRequest request) {
        return productService.addProductToBranch(branchId, request);
    }
}
