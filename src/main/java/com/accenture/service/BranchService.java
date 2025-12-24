package com.accenture.service;

import com.accenture.model.dto.BranchRequest;
import com.accenture.model.dto.BranchResponse;
import reactor.core.publisher.Mono;

public interface BranchService {
    Mono<BranchResponse> addBranchToFranchise(Long franchiseId, BranchRequest request);

    Mono<BranchResponse> updateBranch(Long id, BranchRequest request);
}
