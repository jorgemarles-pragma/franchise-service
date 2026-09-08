package com.pragma.jamarlesf.usecase.updatebranchname;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.exception.InvalidBranchNameException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBranchNameUseCaseTest {

    @Mock
    private BranchModelRepository branchModelRepository;

    private UpdateBranchNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateBranchNameUseCase(branchModelRepository);
    }

    @Test
    @DisplayName("Should update branch name successfully when branch exists and new name is valid")
    void shouldUpdateBranchNameSuccessfullyWhenBranchExistsAndNameIsValid() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_TEN);
        String newName = UseCaseTestConstants.BRANCH_NAME_WITH_SPACES;

        BranchModel existingBranch = BranchModel.builder()
                .id(branchId)
                .name(UseCaseTestConstants.BRANCH_NAME_OLD)
                .franchiseId(new FranchiseModelId(UseCaseTestConstants.ID_ONE))
                .build();

        BranchModel updatedBranch = existingBranch.toBuilder()
                .name(UseCaseTestConstants.BRANCH_NAME_NEW)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(branchModelRepository.update(any(BranchModel.class))).thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(useCase.execute(branchId, newName))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_TEN, result.getId().value());
                    assertEquals(UseCaseTestConstants.BRANCH_NAME_NEW, result.getName());
                    assertEquals(UseCaseTestConstants.ID_ONE, result.getFranchiseId().value());
                })
                .verifyComplete();

        verify(branchModelRepository).findById(branchId);
        verify(branchModelRepository).update(any(BranchModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidBranchNameException when new name is null")
    void shouldEmitInvalidBranchNameExceptionWhenNameIsNull() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_TEN);

        StepVerifier.create(useCase.execute(branchId, null))
                .expectErrorMatches(throwable -> throwable instanceof InvalidBranchNameException
                        && throwable.getMessage().contains(ErrorMessageConstants.BRANCH_NAME_CANNOT_BE_EMPTY_OR_NULL))
                .verify();

        verify(branchModelRepository, never()).findById(any(BranchModelId.class));
        verify(branchModelRepository, never()).update(any(BranchModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidBranchNameException when new name is empty")
    void shouldEmitInvalidBranchNameExceptionWhenNameIsEmpty() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_TEN);

        StepVerifier.create(useCase.execute(branchId, UseCaseTestConstants.EMPTY_STRING))
                .expectErrorMatches(throwable -> throwable instanceof InvalidBranchNameException
                        && throwable.getMessage().contains(ErrorMessageConstants.BRANCH_NAME_CANNOT_BE_EMPTY_OR_NULL))
                .verify();

        verify(branchModelRepository, never()).findById(any(BranchModelId.class));
        verify(branchModelRepository, never()).update(any(BranchModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidBranchNameException when new name is whitespace only")
    void shouldEmitInvalidBranchNameExceptionWhenNameIsWhitespaceOnly() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_TEN);

        StepVerifier.create(useCase.execute(branchId, UseCaseTestConstants.WHITESPACE_STRING))
                .expectErrorMatches(throwable -> throwable instanceof InvalidBranchNameException
                        && throwable.getMessage().contains(ErrorMessageConstants.BRANCH_NAME_CANNOT_BE_EMPTY_OR_NULL))
                .verify();

        verify(branchModelRepository, never()).findById(any(BranchModelId.class));
        verify(branchModelRepository, never()).update(any(BranchModel.class));
    }

    @Test
    @DisplayName("Should emit BranchNotFoundException when branch does not exist")
    void shouldEmitBranchNotFoundExceptionWhenBranchDoesNotExist() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_NON_EXISTENT);
        String newName = UseCaseTestConstants.BRANCH_NAME_NEW;

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(branchId, newName))
                .expectErrorMatches(throwable -> throwable instanceof BranchNotFoundException
                        && throwable.getMessage().contains(UseCaseTestConstants.ID_NON_EXISTENT))
                .verify();

        verify(branchModelRepository).findById(branchId);
        verify(branchModelRepository, never()).update(any(BranchModel.class));
    }
}
