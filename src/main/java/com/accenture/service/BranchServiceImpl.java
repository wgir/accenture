package com.accenture.service;

import com.accenture.exception.ConflictException;
import com.accenture.exception.ResourceNotFoundException;
import com.accenture.model.Branch;
import com.accenture.model.dto.BranchRequest;
import com.accenture.model.dto.BranchResponse;
import com.accenture.repository.BranchRepository;
import com.accenture.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    @Override
    @Transactional
    public Mono<BranchResponse> addBranchToFranchise(Long franchiseId, BranchRequest request) {
        log.info("Adding branch '{}' to franchise id {}", request.getName(), franchiseId);
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found")))
                .flatMap(franchise -> branchRepository
                        .existsByNameIgnoreCaseAndFranchiseId(request.getName(), franchiseId)
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono
                                        .error(new ConflictException("Branch name already exists in this franchise"));
                            }
                            return Mono.defer(() -> branchRepository.save(Branch.builder()
                                    .name(request.getName())
                                    .franchiseId(franchise.getId())
                                    .build()));
                        }))
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public Mono<BranchResponse> updateBranch(Long id, BranchRequest request) {
        log.info("Updating branch id {} with name: {}", id, request.getName());
        return branchRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Branch not found")))
                .flatMap(branch -> {
                    if (branch.getName().equalsIgnoreCase(request.getName())) {
                        return Mono.just(branch);
                    }
                    return branchRepository
                            .existsByNameIgnoreCaseAndFranchiseId(request.getName(), branch.getFranchiseId())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(
                                            new ConflictException("Branch name already exists in this franchise"));
                                }
                                branch.setName(request.getName());
                                return branchRepository.save(branch);
                            });
                })
                .map(this::mapToResponse);
    }

    private BranchResponse mapToResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .franchise_id(branch.getFranchiseId())
                .build();
    }
}
