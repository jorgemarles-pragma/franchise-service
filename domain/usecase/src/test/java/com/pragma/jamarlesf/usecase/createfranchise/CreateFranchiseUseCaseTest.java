package com.pragma.jamarlesf.usecase.createfranchise;

import com.pragma.jamarlesf.model.exception.InvalidFranchiseNameException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
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
                .name("McDonalds")
                .build();

        FranchiseModel createdFranchise = FranchiseModel.builder()
                .id(new FranchiseModelId("1"))
                .name("McDonalds")
                .build();

        when(franchiseModelRepository.create(any(FranchiseModel.class))).thenReturn(Mono.just(createdFranchise));

        StepVerifier.create(useCase.execute(inputFranchise))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("1", result.getId().value());
                    assertEquals("McDonalds", result.getName());
                })
                .verifyComplete();

        verify(franchiseModelRepository).create(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should trim franchise name before creating")
    void shouldTrimNameBeforeCreatingFranchise() {
        FranchiseModel inputFranchise = FranchiseModel.builder()
                .name("  Burger King  ")
                .build();

        FranchiseModel createdFranchise = FranchiseModel.builder()
                .id(new FranchiseModelId("2"))
                .name("Burger King")
                .build();

        when(franchiseModelRepository.create(any(FranchiseModel.class))).thenReturn(Mono.just(createdFranchise));

        StepVerifier.create(useCase.execute(inputFranchise))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("2", result.getId().value());
                    assertEquals("Burger King", result.getName());
                })
                .verifyComplete();

        verify(franchiseModelRepository).create(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidFranchiseNameException when franchise is null")
    void shouldEmitInvalidFranchiseNameExceptionWhenFranchiseIsNull() {
        StepVerifier.create(useCase.execute(null))
                .expectErrorMatches(throwable -> throwable instanceof InvalidFranchiseNameException
                        && throwable.getMessage().contains("Franchise name cannot be empty or null"))
                .verify();

        verify(franchiseModelRepository, never()).create(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidFranchiseNameException when name is null")
    void shouldEmitInvalidFranchiseNameExceptionWhenNameIsNull() {
        FranchiseModel inputFranchise = FranchiseModel.builder()
                .name(null)
                .build();

        StepVerifier.create(useCase.execute(inputFranchise))
                .expectErrorMatches(throwable -> throwable instanceof InvalidFranchiseNameException
                        && throwable.getMessage().contains("Franchise name cannot be empty or null"))
                .verify();

        verify(franchiseModelRepository, never()).create(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidFranchiseNameException when name is empty")
    void shouldEmitInvalidFranchiseNameExceptionWhenNameIsEmpty() {
        FranchiseModel inputFranchise = FranchiseModel.builder()
                .name("")
                .build();

        StepVerifier.create(useCase.execute(inputFranchise))
                .expectErrorMatches(throwable -> throwable instanceof InvalidFranchiseNameException
                        && throwable.getMessage().contains("Franchise name cannot be empty or null"))
                .verify();

        verify(franchiseModelRepository, never()).create(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidFranchiseNameException when name is whitespace only")
    void shouldEmitInvalidFranchiseNameExceptionWhenNameIsWhitespaceOnly() {
        FranchiseModel inputFranchise = FranchiseModel.builder()
                .name("   ")
                .build();

        StepVerifier.create(useCase.execute(inputFranchise))
                .expectErrorMatches(throwable -> throwable instanceof InvalidFranchiseNameException
                        && throwable.getMessage().contains("Franchise name cannot be empty or null"))
                .verify();

        verify(franchiseModelRepository, never()).create(any(FranchiseModel.class));
    }

    @Test
    @DisplayName("Should propagate error when repository fails")
    void shouldPropagateErrorWhenRepositoryFails() {
        FranchiseModel inputFranchise = FranchiseModel.builder()
                .name("KFC")
                .build();

        when(franchiseModelRepository.create(any(FranchiseModel.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(useCase.execute(inputFranchise))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Database error"))
                .verify();

        verify(franchiseModelRepository).create(any(FranchiseModel.class));
    }
}
