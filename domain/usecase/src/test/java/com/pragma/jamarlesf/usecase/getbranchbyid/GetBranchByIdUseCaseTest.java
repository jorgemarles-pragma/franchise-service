package com.pragma.jamarlesf.usecase.getbranchbyid;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.usecase.constant.UseCaseTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBranchByIdUseCaseTest {

    @Mock
    private BranchModelRepository branchModelRepository;

    private GetBranchByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetBranchByIdUseCase(branchModelRepository);
    }

    @Test
    @DisplayName("Should return branch when branch exists")
    void shouldReturnBranchWhenBranchExists() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_TEN);
        BranchModel existingBranch = BranchModel.builder()
                .id(branchId)
                .name(UseCaseTestConstants.BRANCH_NAME_NORTH)
                .franchiseId(new FranchiseModelId(UseCaseTestConstants.ID_ONE))
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));

        StepVerifier.create(useCase.execute(branchId))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_TEN, result.getId().value());
                    assertEquals(UseCaseTestConstants.BRANCH_NAME_NORTH, result.getName());
                    assertEquals(UseCaseTestConstants.ID_ONE, result.getFranchiseId().value());
                })
                .verifyComplete();

        verify(branchModelRepository).findById(branchId);
    }

    @Test
    @DisplayName("Should emit BranchNotFoundException when branch does not exist")
    void shouldEmitBranchNotFoundExceptionWhenBranchDoesNotExist() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_NON_EXISTENT);

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(branchId))
                .expectErrorMatches(throwable -> throwable instanceof BranchNotFoundException
                        && throwable.getMessage().contains(UseCaseTestConstants.ID_NON_EXISTENT))
                .verify();

        verify(branchModelRepository).findById(branchId);
    }

    @Test
    @DisplayName("Should emit BranchNotFoundException when id is null")
    void shouldEmitBranchNotFoundExceptionWhenIdIsNull() {
        when(branchModelRepository.findById(null)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(null))
                .expectErrorMatches(throwable -> throwable instanceof BranchNotFoundException
                        && throwable.getMessage().contains(ErrorMessageConstants.NULL_ID_VALUE))
                .verify();

        verify(branchModelRepository).findById(null);
    }
}
