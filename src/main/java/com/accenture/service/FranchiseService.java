package com.accenture.service;

import com.accenture.model.dto.FranchiseRequest;
import com.accenture.model.dto.FranchiseResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseService {
    Mono<FranchiseResponse> createFranchise(FranchiseRequest request);

    Mono<FranchiseResponse> updateFranchise(Long id, FranchiseRequest request);

    Flux<FranchiseResponse> getAllFranchises();
}
