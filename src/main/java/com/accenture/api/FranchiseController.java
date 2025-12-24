package com.accenture.api;

import com.accenture.model.dto.BranchRequest;
import com.accenture.model.dto.BranchResponse;
import com.accenture.model.dto.FranchiseRequest;
import com.accenture.model.dto.FranchiseResponse;
import com.accenture.model.dto.SpecificProductResponse;
import com.accenture.service.BranchService;
import com.accenture.service.FranchiseService;
import com.accenture.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;

@RestController
@RequestMapping("/v1/franchises")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;
    private final BranchService branchService;
    private final ProductService productService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<FranchiseResponse> getAllFranchises() {
        return franchiseService.getAllFranchises().delayElements(Duration.ofSeconds(1));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranchiseResponse> createFranchise(@Valid @RequestBody FranchiseRequest request) {
        return franchiseService.createFranchise(request);
    }

    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BranchResponse> addBranch(@PathVariable Long franchiseId, @Valid @RequestBody BranchRequest request) {
        return branchService.addBranchToFranchise(franchiseId, request);
    }

    @GetMapping("/{franchiseId}/top-products")
    public Flux<SpecificProductResponse> getTopProducts(@PathVariable Long franchiseId) {
        return productService.getTopProductsByFranchise(franchiseId);
    }

    @PutMapping("/{id}")
    public Mono<FranchiseResponse> updateFranchise(@PathVariable Long id,
            @Valid @RequestBody FranchiseRequest request) {
        return franchiseService.updateFranchise(id, request);
    }
}
