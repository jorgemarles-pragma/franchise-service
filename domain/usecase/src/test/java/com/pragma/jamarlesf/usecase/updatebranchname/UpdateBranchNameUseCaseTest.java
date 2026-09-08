package com.pragma.jamarlesf.usecase.updatebranchname;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.exception.InvalidBranchNameException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
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
        BranchModelId branchId = new BranchModelId("10");
        String newName = "  Sucursal Poblado  ";

        BranchModel existingBranch = BranchModel.builder()
                .id(branchId)
                .name("Sucursal Medellín")
                .franchiseId(new FranchiseModelId("1"))
                .build();

        BranchModel updatedBranch = existingBranch.toBuilder()
                .name("Sucursal Poblado")
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(branchModelRepository.update(any(BranchModel.class))).thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(useCase.execute(branchId, newName))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("10", result.getId().value());
                    assertEquals("Sucursal Poblado", result.getName());
                    assertEquals("1", result.getFranchiseId().value());
                })
                .verifyComplete();

        verify(branchModelRepository).findById(branchId);
        verify(branchModelRepository).update(any(BranchModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidBranchNameException when new name is null")
    void shouldEmitInvalidBranchNameExceptionWhenNameIsNull() {
        BranchModelId branchId = new BranchModelId("10");

        StepVerifier.create(useCase.execute(branchId, null))
                .expectErrorMatches(throwable -> throwable instanceof InvalidBranchNameException
                        && throwable.getMessage().contains("Branch name cannot be empty or null"))
                .verify();

        verify(branchModelRepository, never()).findById(any(BranchModelId.class));
        verify(branchModelRepository, never()).update(any(BranchModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidBranchNameException when new name is empty")
    void shouldEmitInvalidBranchNameExceptionWhenNameIsEmpty() {
        BranchModelId branchId = new BranchModelId("10");

        StepVerifier.create(useCase.execute(branchId, ""))
                .expectErrorMatches(throwable -> throwable instanceof InvalidBranchNameException
                        && throwable.getMessage().contains("Branch name cannot be empty or null"))
                .verify();

        verify(branchModelRepository, never()).findById(any(BranchModelId.class));
        verify(branchModelRepository, never()).update(any(BranchModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidBranchNameException when new name is whitespace only")
    void shouldEmitInvalidBranchNameExceptionWhenNameIsWhitespaceOnly() {
        BranchModelId branchId = new BranchModelId("10");

        StepVerifier.create(useCase.execute(branchId, "   "))
                .expectErrorMatches(throwable -> throwable instanceof InvalidBranchNameException
                        && throwable.getMessage().contains("Branch name cannot be empty or null"))
                .verify();

        verify(branchModelRepository, never()).findById(any(BranchModelId.class));
        verify(branchModelRepository, never()).update(any(BranchModel.class));
    }

    @Test
    @DisplayName("Should emit BranchNotFoundException when branch does not exist")
    void shouldEmitBranchNotFoundExceptionWhenBranchDoesNotExist() {
        BranchModelId branchId = new BranchModelId("999");
        String newName = "Sucursal Poblado";

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(branchId, newName))
                .expectErrorMatches(throwable -> throwable instanceof BranchNotFoundException
                        && throwable.getMessage().contains("999"))
                .verify();

        verify(branchModelRepository).findById(branchId);
        verify(branchModelRepository, never()).update(any(BranchModel.class));
    }
}
