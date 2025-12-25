package com.accenture.service;

import com.accenture.exception.ConflictException;
import com.accenture.exception.ResourceNotFoundException;
import com.accenture.model.Franchise;
import com.accenture.model.dto.FranchiseRequest;
import com.accenture.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

        @Mock
        private FranchiseRepository franchiseRepository;

        @InjectMocks
        private FranchiseServiceImpl franchiseService;

        private FranchiseRequest request;
        private Franchise franchise;

        @BeforeEach
        void setUp() {
                request = FranchiseRequest.builder()
                                .name("Test Franchise")
                                .build();

                franchise = Franchise.builder()
                                .id(1L)
                                .name("Test Franchise")
                                .build();
        }

        @Test
        void createFranchise_Success() {
                when(franchiseRepository.findByNameIgnoreCase(any(String.class))).thenReturn(Mono.empty());
                when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

                StepVerifier.create(franchiseService.createFranchise(request))
                                .expectNextMatches(
                                                response -> response.getName().equals("Test Franchise")
                                                                && response.getId().equals(1L))
                                .verifyComplete();
        }

        @Test
        void createFranchise_Conflict() {
                when(franchiseRepository.findByNameIgnoreCase(any(String.class))).thenReturn(Mono.just(franchise));

                StepVerifier.create(franchiseService.createFranchise(request))
                                .expectError(ConflictException.class)
                                .verify();
        }

        @Test
        void createFranchise_Conflict_IgnoreCase() {
                FranchiseRequest lowercaseRequest = FranchiseRequest.builder()
                                .name("test franchise")
                                .build();
                when(franchiseRepository.findByNameIgnoreCase("test franchise")).thenReturn(Mono.just(franchise));

                StepVerifier.create(franchiseService.createFranchise(lowercaseRequest))
                                .expectError(ConflictException.class)
                                .verify();
        }

        @Test
        void updateFranchise_Success() {
                Franchise updatedFranchise = Franchise.builder()
                                .id(1L)
                                .name("Updated Name")
                                .build();

                when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
                when(franchiseRepository.findByNameIgnoreCase("Updated Name")).thenReturn(Mono.empty());
                when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

                StepVerifier.create(franchiseService.updateFranchise(1L,
                                FranchiseRequest.builder().name("Updated Name").build()))
                                .expectNextMatches(response -> response.getName().equals("Updated Name"))
                                .verifyComplete();
        }

        @Test
        void updateFranchise_Success_SameName() {
                when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
                when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

                StepVerifier.create(franchiseService.updateFranchise(1L,
                                FranchiseRequest.builder().name("Test Franchise").build()))
                                .expectNextMatches(response -> response.getName().equals("Test Franchise"))
                                .verifyComplete();
        }

        @Test
        void updateFranchise_Conflict() {
                when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
                when(franchiseRepository.findByNameIgnoreCase("Existing Name"))
                                .thenReturn(Mono.just(Franchise.builder().id(2L).name("Existing Name").build()));

                StepVerifier.create(franchiseService.updateFranchise(1L,
                                FranchiseRequest.builder().name("Existing Name").build()))
                                .expectError(ConflictException.class)
                                .verify();
        }

        @Test
        void updateFranchise_NotFound() {
                when(franchiseRepository.findById(1L)).thenReturn(Mono.empty());

                StepVerifier.create(franchiseService.updateFranchise(1L, request))
                                .expectError(ResourceNotFoundException.class)
                                .verify();
        }
}
