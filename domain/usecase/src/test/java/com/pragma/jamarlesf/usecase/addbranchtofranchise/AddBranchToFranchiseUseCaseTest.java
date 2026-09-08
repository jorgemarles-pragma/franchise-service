package com.pragma.jamarlesf.usecase.addbranchtofranchise;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
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
class AddBranchToFranchiseUseCaseTest {

    @Mock
    private FranchiseModelRepository franchiseModelRepository;

    @Mock
    private BranchModelRepository branchModelRepository;

    private AddBranchToFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddBranchToFranchiseUseCase(franchiseModelRepository, branchModelRepository);
    }

    @Test
    @DisplayName("Should add branch successfully when franchise exists")
    void shouldAddBranchSuccessfullyWhenFranchiseExists() {
        FranchiseModelId franchiseId = new FranchiseModelId("1");
        FranchiseModel existingFranchise = FranchiseModel.builder()
                .id(franchiseId)
                .name("Franquicia Central")
                .build();

        BranchModel inputBranch = BranchModel.builder()
                .name("Sucursal Norte")
                .build();

        BranchModel createdBranch = BranchModel.builder()
                .id(new BranchModelId("10"))
                .name("Sucursal Norte")
                .franchiseId(franchiseId)
                .build();

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(branchModelRepository.create(any(BranchModel.class))).thenReturn(Mono.just(createdBranch));

        StepVerifier.create(useCase.execute(franchiseId, inputBranch))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("10", result.getId().value());
                    assertEquals("Sucursal Norte", result.getName());
                    assertEquals("1", result.getFranchiseId().value());
                })
                .verifyComplete();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(branchModelRepository).create(any(BranchModel.class));
    }

    @Test
    @DisplayName("Should emit FranchiseNotFoundException when franchise does not exist")
    void shouldEmitFranchiseNotFoundExceptionWhenFranchiseDoesNotExist() {
        FranchiseModelId franchiseId = new FranchiseModelId("999");
        BranchModel inputBranch = BranchModel.builder()
                .name("Sucursal Fantasma")
                .build();

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(franchiseId, inputBranch))
                .expectErrorMatches(throwable -> throwable instanceof FranchiseNotFoundException
                        && throwable.getMessage().contains("999"))
                .verify();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(branchModelRepository, never()).create(any(BranchModel.class));
    }
}
