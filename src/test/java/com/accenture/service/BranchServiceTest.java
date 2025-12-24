package com.accenture.service;

import com.accenture.model.Branch;
import com.accenture.model.Franchise;
import com.accenture.model.dto.BranchRequest;
import com.accenture.repository.BranchRepository;
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
class BranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private R2dbcEntityTemplate entityTemplate;

    @InjectMocks
    private BranchServiceImpl branchService;

    private BranchRequest request;
    private Branch branch;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder().id(1L).name("Franchise A").build();
        request = BranchRequest.builder().name("Branch A").build();
        branch = Branch.builder().id(1L).name("Branch A").franchiseId(1L).build();
    }

    @Test
    void addBranchToFranchise_Success() {
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
        when(entityTemplate.insert(any(Branch.class))).thenReturn(Mono.just(branch));

        StepVerifier.create(branchService.addBranchToFranchise(1L, request))
                .expectNextMatches(
                        response -> response.getName().equals("Branch A") && response.getFranchise_id().equals(1L))
                .verifyComplete();
    }

    @Test
    void updateBranch_Success() {
        Branch updatedBranch = Branch.builder().id(1L).name("Updated Branch").franchiseId(1L).build();
        when(branchRepository.findById(1L)).thenReturn(Mono.just(branch));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(branchService.updateBranch(1L, BranchRequest.builder().name("Updated Branch").build()))
                .expectNextMatches(response -> response.getName().equals("Updated Branch"))
                .verifyComplete();
    }
}
