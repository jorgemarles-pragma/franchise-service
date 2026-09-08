package com.pragma.jamarlesf.usecase.modifyproductstock;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.InvalidProductStockException;
import com.pragma.jamarlesf.model.exception.ProductNotFoundException;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModifyProductStockUseCaseTest {

    @Mock
    private ProductModelRepository productModelRepository;

    private ModifyProductStockUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ModifyProductStockUseCase(productModelRepository);
    }

    @Test
    @DisplayName("Should modify product stock successfully when product exists and stock is positive")
    void shouldModifyStockSuccessfullyWhenProductExistsAndStockIsValid() {
        ProductModelId productId = new ProductModelId(UseCaseTestConstants.ID_ONE_HUNDRED);
        Integer newStock = UseCaseTestConstants.STOCK_ONE_HUNDRED;

        ProductModel existingProduct = ProductModel.builder()
                .id(productId)
                .name(UseCaseTestConstants.PRODUCT_NAME_BURGER)
                .stock(UseCaseTestConstants.STOCK_FIFTY)
                .branchId(new BranchModelId(UseCaseTestConstants.ID_TEN))
                .build();

        ProductModel updatedProduct = existingProduct.toBuilder()
                .stock(newStock)
                .build();

        when(productModelRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productModelRepository.update(any(ProductModel.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(useCase.execute(productId, newStock))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_ONE_HUNDRED, result.getId().value());
                    assertEquals(UseCaseTestConstants.PRODUCT_NAME_BURGER, result.getName());
                    assertEquals(UseCaseTestConstants.STOCK_ONE_HUNDRED, result.getStock());
                    assertEquals(UseCaseTestConstants.ID_TEN, result.getBranchId().value());
                })
                .verifyComplete();

        verify(productModelRepository).findById(productId);
        verify(productModelRepository).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should modify product stock successfully when new stock is zero")
    void shouldModifyStockSuccessfullyWhenStockIsZero() {
        ProductModelId productId = new ProductModelId(UseCaseTestConstants.ID_ONE_HUNDRED);
        Integer newStock = UseCaseTestConstants.STOCK_ZERO;

        ProductModel existingProduct = ProductModel.builder()
                .id(productId)
                .name(UseCaseTestConstants.PRODUCT_NAME_BURGER)
                .stock(UseCaseTestConstants.STOCK_FIFTY)
                .branchId(new BranchModelId(UseCaseTestConstants.ID_TEN))
                .build();

        ProductModel updatedProduct = existingProduct.toBuilder()
                .stock(UseCaseTestConstants.STOCK_ZERO)
                .build();

        when(productModelRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productModelRepository.update(any(ProductModel.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(useCase.execute(productId, newStock))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_ONE_HUNDRED, result.getId().value());
                    assertEquals(UseCaseTestConstants.STOCK_ZERO, result.getStock());
                })
                .verifyComplete();

        verify(productModelRepository).findById(productId);
        verify(productModelRepository).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidProductStockException when new stock is negative")
    void shouldEmitInvalidProductStockExceptionWhenStockIsNegative() {
        ProductModelId productId = new ProductModelId(UseCaseTestConstants.ID_ONE_HUNDRED);
        Integer newStock = UseCaseTestConstants.STOCK_NEGATIVE;

        StepVerifier.create(useCase.execute(productId, newStock))
                .expectErrorMatches(throwable -> throwable instanceof InvalidProductStockException
                        && throwable.getMessage().contains(ErrorMessageConstants.PRODUCT_STOCK_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO))
                .verify();

        verify(productModelRepository, never()).findById(any(ProductModelId.class));
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidProductStockException when new stock is null")
    void shouldEmitInvalidProductStockExceptionWhenStockIsNull() {
        ProductModelId productId = new ProductModelId(UseCaseTestConstants.ID_ONE_HUNDRED);

        StepVerifier.create(useCase.execute(productId, null))
                .expectErrorMatches(throwable -> throwable instanceof InvalidProductStockException
                        && throwable.getMessage().contains(ErrorMessageConstants.PRODUCT_STOCK_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO))
                .verify();

        verify(productModelRepository, never()).findById(any(ProductModelId.class));
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit ProductNotFoundException when product does not exist")
    void shouldEmitProductNotFoundExceptionWhenProductDoesNotExist() {
        ProductModelId productId = new ProductModelId(UseCaseTestConstants.ID_NON_EXISTENT);
        Integer newStock = UseCaseTestConstants.STOCK_TWENTY;

        when(productModelRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(productId, newStock))
                .expectErrorMatches(throwable -> throwable instanceof ProductNotFoundException
                        && throwable.getMessage().contains(UseCaseTestConstants.ID_NON_EXISTENT))
                .verify();

        verify(productModelRepository).findById(productId);
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }
}
