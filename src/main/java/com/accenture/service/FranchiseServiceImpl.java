package com.accenture.service;

import com.accenture.exception.ResourceNotFoundException;
import com.accenture.model.Franchise;
import com.accenture.model.dto.FranchiseRequest;
import com.accenture.model.dto.FranchiseResponse;
import com.accenture.repository.FranchiseRepository;
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
public class FranchiseServiceImpl implements FranchiseService {

    private final FranchiseRepository franchiseRepository;
    private final R2dbcEntityTemplate entityTemplate;

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
        Franchise franchise = Franchise.builder()
                .name(request.getName())
                .build();
        return entityTemplate.insert(franchise)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public Mono<FranchiseResponse> updateFranchise(Long id, FranchiseRequest request) {
        log.info("Updating franchise id {} with name: {}", id, request.getName());
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found")))
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
