package com.pragma.jamarlesf.usecase.getbranchesbyfranchise;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import com.pragma.jamarlesf.usecase.constant.UseCaseTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBranchesByFranchiseUseCaseTest {

    @Mock
    private FranchiseModelRepository franchiseModelRepository;

    @Mock
    private BranchModelRepository branchModelRepository;

    private GetBranchesByFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetBranchesByFranchiseUseCase(franchiseModelRepository, branchModelRepository);
    }

    @Test
    @DisplayName("Should return flux of branches when franchise exists and branches exist")
    void shouldReturnBranchesWhenFranchiseExists() {
        FranchiseModelId franchiseId = new FranchiseModelId(UseCaseTestConstants.ID_ONE);
        FranchiseModel existingFranchise = FranchiseModel.builder()
                .id(franchiseId)
                .name(UseCaseTestConstants.FRANCHISE_NAME_MCDONALDS)
                .build();

        BranchModel branch1 = BranchModel.builder()
                .id(new BranchModelId(UseCaseTestConstants.ID_TEN))
                .name(UseCaseTestConstants.BRANCH_NAME_NORTH)
                .franchiseId(franchiseId)
                .build();

        BranchModel branch2 = BranchModel.builder()
                .id(new BranchModelId(UseCaseTestConstants.ID_TWENTY))
                .name(UseCaseTestConstants.BRANCH_NAME_DOWNTOWN)
                .franchiseId(franchiseId)
                .build();

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(branchModelRepository.findAllByFranchiseId(franchiseId)).thenReturn(Flux.just(branch1, branch2));

        StepVerifier.create(useCase.execute(franchiseId))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_TEN, result.getId().value());
                    assertEquals(UseCaseTestConstants.BRANCH_NAME_NORTH, result.getName());
                    assertEquals(UseCaseTestConstants.ID_ONE, result.getFranchiseId().value());
                })
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_TWENTY, result.getId().value());
                    assertEquals(UseCaseTestConstants.BRANCH_NAME_DOWNTOWN, result.getName());
                    assertEquals(UseCaseTestConstants.ID_ONE, result.getFranchiseId().value());
                })
                .verifyComplete();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(branchModelRepository).findAllByFranchiseId(franchiseId);
    }

    @Test
    @DisplayName("Should return empty flux when franchise exists but has no branches")
    void shouldReturnEmptyFluxWhenFranchiseHasNoBranches() {
        FranchiseModelId franchiseId = new FranchiseModelId(UseCaseTestConstants.ID_ONE);
        FranchiseModel existingFranchise = FranchiseModel.builder()
                .id(franchiseId)
                .name(UseCaseTestConstants.FRANCHISE_NAME_MCDONALDS)
                .build();

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(branchModelRepository.findAllByFranchiseId(franchiseId)).thenReturn(Flux.empty());

        StepVerifier.create(useCase.execute(franchiseId))
                .verifyComplete();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(branchModelRepository).findAllByFranchiseId(franchiseId);
    }

    @Test
    @DisplayName("Should emit FranchiseNotFoundException when franchise does not exist")
    void shouldEmitFranchiseNotFoundExceptionWhenFranchiseDoesNotExist() {
        FranchiseModelId franchiseId = new FranchiseModelId(UseCaseTestConstants.ID_NON_EXISTENT);

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(franchiseId))
                .expectErrorMatches(throwable -> throwable instanceof FranchiseNotFoundException
                        && throwable.getMessage().contains(UseCaseTestConstants.ID_NON_EXISTENT))
                .verify();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(branchModelRepository, never()).findAllByFranchiseId(franchiseId);
    }

    @Test
    @DisplayName("Should emit FranchiseNotFoundException when franchiseId is null")
    void shouldEmitFranchiseNotFoundExceptionWhenFranchiseIdIsNull() {
        when(franchiseModelRepository.findById(null)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(null))
                .expectErrorMatches(throwable -> throwable instanceof FranchiseNotFoundException
                        && throwable.getMessage().contains(ErrorMessageConstants.NULL_ID_VALUE))
                .verify();

        verify(franchiseModelRepository).findById(null);
        verify(branchModelRepository, never()).findAllByFranchiseId(null);
    }
}
