package com.pragma.jamarlesf.usecase.gethigheststockproductsbyfranchise;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetHighestStockProductsByFranchiseUseCaseTest {

    @Mock
    private FranchiseModelRepository franchiseModelRepository;

    @Mock
    private ProductModelRepository productModelRepository;

    private GetHighestStockProductsByFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetHighestStockProductsByFranchiseUseCase(franchiseModelRepository, productModelRepository);
    }

    @Test
    @DisplayName("Should return highest stock products when franchise exists")
    void shouldReturnHighestStockProductsWhenFranchiseExists() {
        FranchiseModelId franchiseId = new FranchiseModelId("1");
        FranchiseModel existingFranchise = FranchiseModel.builder()
                .id(franchiseId)
                .name("Franquicia Central")
                .build();

        ProductModel productBranch1 = ProductModel.builder()
                .id(new ProductModelId("101"))
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(new BranchModelId("10"))
                .build();

        ProductModel productBranch2 = ProductModel.builder()
                .id(new ProductModelId("201"))
                .name("Papas Medianas")
                .stock(80)
                .branchId(new BranchModelId("20"))
                .build();

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(productModelRepository.findHighestStockByFranchiseId(franchiseId)).thenReturn(Flux.just(productBranch1, productBranch2));

        StepVerifier.create(useCase.execute(franchiseId))
                .assertNext(p1 -> {
                    assertNotNull(p1);
                    assertEquals("101", p1.getId().value());
                    assertEquals("Hamburguesa Doble", p1.getName());
                    assertEquals(50, p1.getStock());
                    assertEquals("10", p1.getBranchId().value());
                })
                .assertNext(p2 -> {
                    assertNotNull(p2);
                    assertEquals("201", p2.getId().value());
                    assertEquals("Papas Medianas", p2.getName());
                    assertEquals(80, p2.getStock());
                    assertEquals("20", p2.getBranchId().value());
                })
                .verifyComplete();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(productModelRepository).findHighestStockByFranchiseId(franchiseId);
    }

    @Test
    @DisplayName("Should emit FranchiseNotFoundException when franchise does not exist")
    void shouldEmitFranchiseNotFoundExceptionWhenFranchiseDoesNotExist() {
        FranchiseModelId franchiseId = new FranchiseModelId("999");
        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(franchiseId))
                .expectErrorMatches(throwable -> throwable instanceof FranchiseNotFoundException
                        && throwable.getMessage().contains("999"))
                .verify();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(productModelRepository, never()).findHighestStockByFranchiseId(any());
    }

    @Test
    @DisplayName("Should emit FranchiseNotFoundException when franchiseId is null")
    void shouldEmitFranchiseNotFoundExceptionWhenFranchiseIdIsNull() {
        StepVerifier.create(useCase.execute(null))
                .expectError(FranchiseNotFoundException.class)
                .verify();

        verify(franchiseModelRepository, never()).findById(any());
        verify(productModelRepository, never()).findHighestStockByFranchiseId(any());
    }

    @Test
    @DisplayName("Should emit FranchiseNotFoundException when franchiseId value is blank")
    void shouldEmitFranchiseNotFoundExceptionWhenFranchiseIdValueIsBlank() {
        StepVerifier.create(useCase.execute(new FranchiseModelId("  ")))
                .expectError(FranchiseNotFoundException.class)
                .verify();

        verify(franchiseModelRepository, never()).findById(any());
        verify(productModelRepository, never()).findHighestStockByFranchiseId(any());
    }

    @Test
    @DisplayName("Should return empty flux when franchise exists but has no products")
    void shouldReturnEmptyFluxWhenFranchiseExistsButHasNoProducts() {
        FranchiseModelId franchiseId = new FranchiseModelId("1");
        FranchiseModel existingFranchise = FranchiseModel.builder()
                .id(franchiseId)
                .name("Franquicia Central")
                .build();

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(productModelRepository.findHighestStockByFranchiseId(franchiseId)).thenReturn(Flux.empty());

        StepVerifier.create(useCase.execute(franchiseId))
                .verifyComplete();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(productModelRepository).findHighestStockByFranchiseId(franchiseId);
    }

    @Test
    @DisplayName("Should propagate error when product repository fails")
    void shouldPropagateErrorWhenProductRepositoryFails() {
        FranchiseModelId franchiseId = new FranchiseModelId("1");
        FranchiseModel existingFranchise = FranchiseModel.builder()
                .id(franchiseId)
                .name("Franquicia Central")
                .build();

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(productModelRepository.findHighestStockByFranchiseId(franchiseId)).thenReturn(Flux.error(new RuntimeException("Database error")));

        StepVerifier.create(useCase.execute(franchiseId))
                .expectErrorMatches(error -> error instanceof RuntimeException && error.getMessage().equals("Database error"))
                .verify();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(productModelRepository).findHighestStockByFranchiseId(franchiseId);
    }
}
