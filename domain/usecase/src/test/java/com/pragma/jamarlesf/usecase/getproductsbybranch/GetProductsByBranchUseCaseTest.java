package com.pragma.jamarlesf.usecase.getproductsbybranch;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductsByBranchUseCaseTest {

    @Mock
    private BranchModelRepository branchModelRepository;

    @Mock
    private ProductModelRepository productModelRepository;

    private GetProductsByBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetProductsByBranchUseCase(branchModelRepository, productModelRepository);
    }

    @Test
    @DisplayName("Should return products when branch exists")
    void shouldReturnProductsWhenBranchExists() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_TEN);
        BranchModel branch = BranchModel.builder()
                .id(branchId)
                .name(UseCaseTestConstants.BRANCH_NAME_NORTH)
                .build();

        ProductModel product1 = ProductModel.builder()
                .id(new ProductModelId(UseCaseTestConstants.ID_ONE_HUNDRED))
                .name(UseCaseTestConstants.PRODUCT_NAME_BURGER)
                .stock(UseCaseTestConstants.STOCK_FIFTY)
                .branchId(branchId)
                .build();

        ProductModel product2 = ProductModel.builder()
                .id(new ProductModelId(UseCaseTestConstants.ID_TWO))
                .name(UseCaseTestConstants.PRODUCT_NAME_FRIES)
                .stock(UseCaseTestConstants.STOCK_TWENTY)
                .branchId(branchId)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productModelRepository.findAllByBranchId(branchId)).thenReturn(Flux.just(product1, product2));

        StepVerifier.create(useCase.execute(branchId))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_ONE_HUNDRED, result.getId().value());
                    assertEquals(UseCaseTestConstants.PRODUCT_NAME_BURGER, result.getName());
                    assertEquals(UseCaseTestConstants.STOCK_FIFTY, result.getStock());
                })
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_TWO, result.getId().value());
                    assertEquals(UseCaseTestConstants.PRODUCT_NAME_FRIES, result.getName());
                    assertEquals(UseCaseTestConstants.STOCK_TWENTY, result.getStock());
                })
                .verifyComplete();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository).findAllByBranchId(branchId);
    }

    @Test
    @DisplayName("Should return empty flux when branch exists but has no products")
    void shouldReturnEmptyFluxWhenBranchHasNoProducts() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_TEN);
        BranchModel branch = BranchModel.builder()
                .id(branchId)
                .name(UseCaseTestConstants.BRANCH_NAME_NORTH)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productModelRepository.findAllByBranchId(branchId)).thenReturn(Flux.empty());

        StepVerifier.create(useCase.execute(branchId))
                .verifyComplete();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository).findAllByBranchId(branchId);
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
        verify(productModelRepository, never()).findAllByBranchId(branchId);
    }

    @Test
    @DisplayName("Should emit BranchNotFoundException when branch id is null")
    void shouldEmitBranchNotFoundExceptionWhenBranchIdIsNull() {
        when(branchModelRepository.findById(null)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(null))
                .expectErrorMatches(throwable -> throwable instanceof BranchNotFoundException
                        && throwable.getMessage().contains(ErrorMessageConstants.NULL_ID_VALUE))
                .verify();

        verify(branchModelRepository).findById(null);
        verify(productModelRepository, never()).findAllByBranchId(null);
    }
}
