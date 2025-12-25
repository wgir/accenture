package com.accenture.service;

import com.accenture.exception.ConflictException;
import com.accenture.exception.ResourceNotFoundException;
import com.accenture.model.Franchise;
import com.accenture.model.dto.FranchiseRequest;
import com.accenture.model.dto.FranchiseResponse;
import com.accenture.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FranchiseServiceImpl implements FranchiseService {

    private final FranchiseRepository franchiseRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<FranchiseResponse> getAllFranchises() {
        log.info("Getting all franchises");
        return franchiseRepository.findAll()
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public Mono<FranchiseResponse> createFranchise(FranchiseRequest request) {
        log.info("Creating franchise with name: {}", request.getName());
        return franchiseRepository.findByNameIgnoreCase(request.getName())
                .flatMap(existing -> Mono
                        .<FranchiseResponse>error(new ConflictException("Franchise name already exists")))
                .switchIfEmpty(Mono.defer(() -> franchiseRepository.save(Franchise.builder()
                        .name(request.getName())
                        .build())
                        .map(this::mapToResponse)));
    }

    @Override
    @Transactional
    public Mono<FranchiseResponse> updateFranchise(Long id, FranchiseRequest request) {
        log.info("Updating franchise id {} with name: {}", id, request.getName());
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found")))
                .flatMap(franchise -> {
                    if (franchise.getName().equalsIgnoreCase(request.getName())) {
                        return Mono.just(franchise);
                    }
                    return franchiseRepository.findByNameIgnoreCase(request.getName())
                            .flatMap(existing -> Mono
                                    .<Franchise>error(new ConflictException("Franchise name already exists")))
                            .switchIfEmpty(Mono.just(franchise));
                })
                .flatMap(franchise -> {
                    franchise.setName(request.getName());
                    return franchiseRepository.save(franchise);
                })
                .map(this::mapToResponse);
    }

    private FranchiseResponse mapToResponse(Franchise franchise) {
        return FranchiseResponse.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .build();
    }
}
