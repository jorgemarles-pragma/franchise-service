package com.pragma.jamarlesf.usecase.gethigheststockproductsbyfranchise;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
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
        FranchiseModelId franchiseId = new FranchiseModelId(UseCaseTestConstants.ID_ONE);
        FranchiseModel existingFranchise = FranchiseModel.builder()
                .id(franchiseId)
                .name(UseCaseTestConstants.FRANCHISE_NAME_MCDONALDS)
                .build();

        ProductModel productBranch1 = ProductModel.builder()
                .id(new ProductModelId(UseCaseTestConstants.ID_ONE_HUNDRED))
                .name(UseCaseTestConstants.PRODUCT_NAME_BURGER)
                .stock(UseCaseTestConstants.STOCK_FIFTY)
                .branchId(new BranchModelId(UseCaseTestConstants.ID_TEN))
                .build();

        ProductModel productBranch2 = ProductModel.builder()
                .id(new ProductModelId(UseCaseTestConstants.ID_TWO))
                .name(UseCaseTestConstants.PRODUCT_NAME_FRIES)
                .stock(UseCaseTestConstants.STOCK_ONE_HUNDRED)
                .branchId(new BranchModelId(UseCaseTestConstants.ID_TWENTY))
                .build();

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(productModelRepository.findHighestStockByFranchiseId(franchiseId)).thenReturn(Flux.just(productBranch1, productBranch2));

        StepVerifier.create(useCase.execute(franchiseId))
                .assertNext(p1 -> {
                    assertNotNull(p1);
                    assertEquals(UseCaseTestConstants.ID_ONE_HUNDRED, p1.getId().value());
                    assertEquals(UseCaseTestConstants.PRODUCT_NAME_BURGER, p1.getName());
                    assertEquals(UseCaseTestConstants.STOCK_FIFTY, p1.getStock());
                    assertEquals(UseCaseTestConstants.ID_TEN, p1.getBranchId().value());
                })
                .assertNext(p2 -> {
                    assertNotNull(p2);
                    assertEquals(UseCaseTestConstants.ID_TWO, p2.getId().value());
                    assertEquals(UseCaseTestConstants.PRODUCT_NAME_FRIES, p2.getName());
                    assertEquals(UseCaseTestConstants.STOCK_ONE_HUNDRED, p2.getStock());
                    assertEquals(UseCaseTestConstants.ID_TWENTY, p2.getBranchId().value());
                })
                .verifyComplete();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(productModelRepository).findHighestStockByFranchiseId(franchiseId);
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
        StepVerifier.create(useCase.execute(new FranchiseModelId(UseCaseTestConstants.WHITESPACE_STRING)))
                .expectError(FranchiseNotFoundException.class)
                .verify();

        verify(franchiseModelRepository, never()).findById(any());
        verify(productModelRepository, never()).findHighestStockByFranchiseId(any());
    }

    @Test
    @DisplayName("Should return empty flux when franchise exists but has no products")
    void shouldReturnEmptyFluxWhenFranchiseExistsButHasNoProducts() {
        FranchiseModelId franchiseId = new FranchiseModelId(UseCaseTestConstants.ID_ONE);
        FranchiseModel existingFranchise = FranchiseModel.builder()
                .id(franchiseId)
                .name(UseCaseTestConstants.FRANCHISE_NAME_MCDONALDS)
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
        FranchiseModelId franchiseId = new FranchiseModelId(UseCaseTestConstants.ID_ONE);
        FranchiseModel existingFranchise = FranchiseModel.builder()
                .id(franchiseId)
                .name(UseCaseTestConstants.FRANCHISE_NAME_MCDONALDS)
                .build();

        when(franchiseModelRepository.findById(franchiseId)).thenReturn(Mono.just(existingFranchise));
        when(productModelRepository.findHighestStockByFranchiseId(franchiseId)).thenReturn(Flux.error(new RuntimeException(UseCaseTestConstants.DATABASE_ERROR_MSG)));

        StepVerifier.create(useCase.execute(franchiseId))
                .expectErrorMatches(error -> error instanceof RuntimeException && error.getMessage().equals(UseCaseTestConstants.DATABASE_ERROR_MSG))
                .verify();

        verify(franchiseModelRepository).findById(franchiseId);
        verify(productModelRepository).findHighestStockByFranchiseId(franchiseId);
    }
}
