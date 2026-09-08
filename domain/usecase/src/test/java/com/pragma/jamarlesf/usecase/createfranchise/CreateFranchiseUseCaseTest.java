package com.pragma.jamarlesf.usecase.createfranchise;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.InvalidFranchiseNameException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import com.pragma.jamarlesf.usecase.constant.UseCaseTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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
class CreateFranchiseUseCaseTest {

    @Mock
    private FranchiseModelRepository franchiseModelRepository;

    private CreateFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateFranchiseUseCase(franchiseModelRepository);
    }

    @Test
    @DisplayName("Should create franchise successfully when name is valid")
    void shouldCreateFranchiseSuccessfullyWhenNameIsValid() {
        FranchiseModel inputFranchise = FranchiseModel.builder()
                .name(UseCaseTestConstants.FRANCHISE_NAME_MCDONALDS)
                .build();

        FranchiseModel createdFranchise = FranchiseModel.builder()
                .id(new FranchiseModelId(UseCaseTestConstants.ID_ONE))
                .name(UseCaseTestConstants.FRANCHISE_NAME_MCDONALDS)
                .build();

        when(franchiseModelRepository.create(any(FranchiseModel.class))).thenReturn(Mono.just(createdFranchise));

        StepVerifier.create(useCase.execute(inputFranchise))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_ONE, result.getId().value());
                    assertEquals(UseCaseTestConstants.FRANCHISE_NAME_MCDONALDS, result.getName());
                })
                .verifyComplete();

        verify(franchiseModelRepository).create(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should trim franchise name before creating")
    void shouldTrimNameBeforeCreatingFranchise() {
        FranchiseModel inputFranchise = FranchiseModel.builder()
                .name(UseCaseTestConstants.FRANCHISE_NAME_WITH_SPACES)
                .build();

        FranchiseModel createdFranchise = FranchiseModel.builder()
                .id(new FranchiseModelId(UseCaseTestConstants.ID_TWO))
                .name(UseCaseTestConstants.FRANCHISE_NAME_BURGER_KING)
                .build();

        when(franchiseModelRepository.create(any(FranchiseModel.class))).thenReturn(Mono.just(createdFranchise));

        StepVerifier.create(useCase.execute(inputFranchise))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_TWO, result.getId().value());
                    assertEquals(UseCaseTestConstants.FRANCHISE_NAME_BURGER_KING, result.getName());
                })
                .verifyComplete();

        verify(franchiseModelRepository).create(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidFranchiseNameException when franchise is null")
    void shouldEmitInvalidFranchiseNameExceptionWhenFranchiseIsNull() {
        StepVerifier.create(useCase.execute(null))
                .expectErrorMatches(throwable -> throwable instanceof InvalidFranchiseNameException
                        && throwable.getMessage().contains(ErrorMessageConstants.FRANCHISE_NAME_CANNOT_BE_EMPTY_OR_NULL))
                .verify();

        verify(franchiseModelRepository, never()).create(any(FranchiseModel.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {UseCaseTestConstants.WHITESPACE_STRING, "\t\n"})
    @DisplayName("Should emit InvalidFranchiseNameException when name is null, empty, or blank")
    void shouldEmitInvalidFranchiseNameExceptionWhenNameIsInvalid(String invalidName) {
        FranchiseModel inputFranchise = FranchiseModel.builder()
                .name(invalidName)
                .build();

        StepVerifier.create(useCase.execute(inputFranchise))
                .expectErrorMatches(throwable -> throwable instanceof InvalidFranchiseNameException
                        && throwable.getMessage().contains(ErrorMessageConstants.FRANCHISE_NAME_CANNOT_BE_EMPTY_OR_NULL))
                .verify();

        verify(franchiseModelRepository, never()).create(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should propagate error when repository fails")
    void shouldPropagateErrorWhenRepositoryFails() {
        FranchiseModel inputFranchise = FranchiseModel.builder()
                .name(UseCaseTestConstants.FRANCHISE_NAME_KFC)
                .build();

        when(franchiseModelRepository.create(any(FranchiseModel.class)))
                .thenReturn(Mono.error(new RuntimeException(UseCaseTestConstants.DATABASE_ERROR_MSG)));

        StepVerifier.create(useCase.execute(inputFranchise))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals(UseCaseTestConstants.DATABASE_ERROR_MSG))
                .verify();

        verify(franchiseModelRepository).create(any(FranchiseModel.class));
    }
}
