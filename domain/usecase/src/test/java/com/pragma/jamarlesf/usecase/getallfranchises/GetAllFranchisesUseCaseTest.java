package com.pragma.jamarlesf.usecase.getallfranchises;

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
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllFranchisesUseCaseTest {

    @Mock
    private FranchiseModelRepository franchiseModelRepository;

    private GetAllFranchisesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetAllFranchisesUseCase(franchiseModelRepository);
    }

    @Test
    @DisplayName("Should return all franchises successfully")
    void shouldReturnAllFranchisesSuccessfully() {
        FranchiseModel franchise1 = FranchiseModel.builder()
                .id(new FranchiseModelId(UseCaseTestConstants.ID_ONE))
                .name(UseCaseTestConstants.FRANCHISE_NAME_MCDONALDS)
                .build();

        FranchiseModel franchise2 = FranchiseModel.builder()
                .id(new FranchiseModelId(UseCaseTestConstants.ID_TWO))
                .name(UseCaseTestConstants.FRANCHISE_NAME_BURGER_KING)
                .build();

        when(franchiseModelRepository.findAll()).thenReturn(Flux.just(franchise1, franchise2));

        StepVerifier.create(useCase.execute())
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_ONE, result.getId().value());
                    assertEquals(UseCaseTestConstants.FRANCHISE_NAME_MCDONALDS, result.getName());
                })
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_TWO, result.getId().value());
                    assertEquals(UseCaseTestConstants.FRANCHISE_NAME_BURGER_KING, result.getName());
                })
                .verifyComplete();

        verify(franchiseModelRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty flux when no franchises exist")
    void shouldReturnEmptyFluxWhenNoFranchisesExist() {
        when(franchiseModelRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(useCase.execute())
                .verifyComplete();

        verify(franchiseModelRepository).findAll();
    }

    @Test
    @DisplayName("Should propagate error when repository fails")
    void shouldPropagateErrorWhenRepositoryFails() {
        when(franchiseModelRepository.findAll())
                .thenReturn(Flux.error(new RuntimeException(UseCaseTestConstants.DATABASE_ERROR_MSG)));

        StepVerifier.create(useCase.execute())
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals(UseCaseTestConstants.DATABASE_ERROR_MSG))
                .verify();

        verify(franchiseModelRepository).findAll();
    }
}
