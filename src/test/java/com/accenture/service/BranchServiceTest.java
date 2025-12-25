package com.accenture.service;

import com.accenture.exception.ConflictException;
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

    // @Mock
    // private R2dbcEntityTemplate entityTemplate;

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
        when(branchRepository.existsByNameIgnoreCaseAndFranchiseId("Branch A", 1L)).thenReturn(Mono.just(false));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(branch));

        StepVerifier.create(branchService.addBranchToFranchise(1L, request))
                .expectNextMatches(
                        response -> response.getName().equals("Branch A") && response.getFranchise_id().equals(1L))
                .verifyComplete();
    }

    @Test
    void addBranchToFranchise_Conflict() {
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
        when(branchRepository.existsByNameIgnoreCaseAndFranchiseId("Branch A", 1L)).thenReturn(Mono.just(true));

        StepVerifier.create(branchService.addBranchToFranchise(1L, request))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void addBranchToFranchise_Conflict_IgnoreCase() {
        BranchRequest lowercaseRequest = BranchRequest.builder().name("branch a").build();
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
        when(branchRepository.existsByNameIgnoreCaseAndFranchiseId("branch a", 1L)).thenReturn(Mono.just(true));

        StepVerifier.create(branchService.addBranchToFranchise(1L, lowercaseRequest))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void updateBranch_Success() {
        Branch updatedBranch = Branch.builder().id(1L).name("Updated Branch").franchiseId(1L).build();
        when(branchRepository.findById(1L)).thenReturn(Mono.just(branch));
        when(branchRepository.existsByNameIgnoreCaseAndFranchiseId("Updated Branch", 1L)).thenReturn(Mono.just(false));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(branchService.updateBranch(1L, BranchRequest.builder().name("Updated Branch").build()))
                .expectNextMatches(response -> response.getName().equals("Updated Branch"))
                .verifyComplete();
    }

    @Test
    void updateBranch_Success_SameName() {
        when(branchRepository.findById(1L)).thenReturn(Mono.just(branch));
        // No call to existsByNameIgnoreCaseAndFranchiseId expected as name didn't
        // change (ignoring case)

        StepVerifier.create(branchService.updateBranch(1L, BranchRequest.builder().name("BRANCH A").build()))
                .expectNextMatches(response -> response.getName().equals("Branch A"))
                .verifyComplete();
    }

    @Test
    void updateBranch_Conflict() {
        when(branchRepository.findById(1L)).thenReturn(Mono.just(branch));
        when(branchRepository.existsByNameIgnoreCaseAndFranchiseId("Existing Branch", 1L)).thenReturn(Mono.just(true));

        StepVerifier.create(branchService.updateBranch(1L, BranchRequest.builder().name("Existing Branch").build()))
                .expectError(ConflictException.class)
                .verify();
    }
}
