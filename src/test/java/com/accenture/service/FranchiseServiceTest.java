package com.accenture.service;

import com.accenture.model.Franchise;
import com.accenture.model.dto.FranchiseRequest;
import com.accenture.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

        @Mock
        private FranchiseRepository franchiseRepository;

        @Mock
        private R2dbcEntityTemplate entityTemplate;

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
                when(entityTemplate.insert(any(Franchise.class))).thenReturn(Mono.just(franchise));

                StepVerifier.create(franchiseService.createFranchise(request))
                                .expectNextMatches(
                                                response -> response.getName().equals("Test Franchise")
                                                                && response.getId().equals(1L))
                                .verifyComplete();
        }

        @Test
        void updateFranchise_Success() {
                Franchise updatedFranchise = Franchise.builder()
                                .id(1L)
                                .name("Updated Name")
                                .build();

                when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
                when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

                StepVerifier.create(franchiseService.updateFranchise(1L,
                                FranchiseRequest.builder().name("Updated Name").build()))
                                .expectNextMatches(response -> response.getName().equals("Updated Name"))
                                .verifyComplete();
        }
}
