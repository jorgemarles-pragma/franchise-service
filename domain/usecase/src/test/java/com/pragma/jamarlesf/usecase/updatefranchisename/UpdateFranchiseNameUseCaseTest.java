package com.pragma.jamarlesf.usecase.updatefranchisename;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.exception.InvalidFranchiseNameException;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateFranchiseNameUseCaseTest {

    @Mock
    private FranchiseModelRepository franchiseModelRepository;

    private UpdateFranchiseNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateFranchiseNameUseCase(franchiseModelRepository);
    }

    @Test
    @DisplayName("Should update franchise name successfully when franchise exists and new name is valid")
    void shouldUpdateFranchiseNameSuccessfullyWhenFranchiseExistsAndNameIsValid() {
        FranchiseModelId franchiseId = new FranchiseModelId(UseCaseTestConstants.ID_ONE);
        String newName = UseCaseTestConstants.FRANCHISE_NAME_WITH_SPACES;

        FranchiseModel existingFranchise = FranchiseModel.builder()
                .id(franchiseId)
                .name(UseCaseTestConstants.FRANCHISE_NAME_OLD)
                .build();

        FranchiseModel updatedFranchise = existingFranchise.toBuilder()
                .name(UseCaseTestConstants.FRANCHISE_NAME_NEW)
                .build();

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(franchiseModelRepository.update(any(FranchiseModel.class))).thenReturn(Mono.just(updatedFranchise));

        StepVerifier.create(useCase.execute(franchiseId, newName))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_ONE, result.getId().value());
                    assertEquals(UseCaseTestConstants.FRANCHISE_NAME_NEW, result.getName());
                })
                .verifyComplete();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(franchiseModelRepository).update(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidFranchiseNameException when new name is null")
    void shouldEmitInvalidFranchiseNameExceptionWhenNameIsNull() {
        FranchiseModelId franchiseId = new FranchiseModelId(UseCaseTestConstants.ID_ONE);

        StepVerifier.create(useCase.execute(franchiseId, null))
                .expectErrorMatches(throwable -> throwable instanceof InvalidFranchiseNameException
                        && throwable.getMessage().contains(ErrorMessageConstants.FRANCHISE_NAME_CANNOT_BE_EMPTY_OR_NULL))
                .verify();

        verify(franchiseModelRepository, never()).findById(any(FranchiseModelId.class));
        verify(franchiseModelRepository, never()).update(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidFranchiseNameException when new name is empty")
    void shouldEmitInvalidFranchiseNameExceptionWhenNameIsEmpty() {
        FranchiseModelId franchiseId = new FranchiseModelId(UseCaseTestConstants.ID_ONE);

        StepVerifier.create(useCase.execute(franchiseId, UseCaseTestConstants.EMPTY_STRING))
                .expectErrorMatches(throwable -> throwable instanceof InvalidFranchiseNameException
                        && throwable.getMessage().contains(ErrorMessageConstants.FRANCHISE_NAME_CANNOT_BE_EMPTY_OR_NULL))
                .verify();

        verify(franchiseModelRepository, never()).findById(any(FranchiseModelId.class));
        verify(franchiseModelRepository, never()).update(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidFranchiseNameException when new name is whitespace only")
    void shouldEmitInvalidFranchiseNameExceptionWhenNameIsWhitespaceOnly() {
        FranchiseModelId franchiseId = new FranchiseModelId(UseCaseTestConstants.ID_ONE);

        StepVerifier.create(useCase.execute(franchiseId, UseCaseTestConstants.WHITESPACE_STRING))
                .expectErrorMatches(throwable -> throwable instanceof InvalidFranchiseNameException
                        && throwable.getMessage().contains(ErrorMessageConstants.FRANCHISE_NAME_CANNOT_BE_EMPTY_OR_NULL))
                .verify();

        verify(franchiseModelRepository, never()).findById(any(FranchiseModelId.class));
        verify(franchiseModelRepository, never()).update(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should emit FranchiseNotFoundException when franchise does not exist")
    void shouldEmitFranchiseNotFoundExceptionWhenFranchiseDoesNotExist() {
        FranchiseModelId franchiseId = new FranchiseModelId(UseCaseTestConstants.ID_NON_EXISTENT);
        String newName = UseCaseTestConstants.FRANCHISE_NAME_NEW;

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(franchiseId, newName))
                .expectErrorMatches(throwable -> throwable instanceof FranchiseNotFoundException
                        && throwable.getMessage().contains(UseCaseTestConstants.ID_NON_EXISTENT))
                .verify();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(franchiseModelRepository, never()).update(any(FranchiseModel.class));
    }
}
