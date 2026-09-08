package com.pragma.jamarlesf.usecase.modifyproductstock;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.exception.InvalidProductStockException;
import com.pragma.jamarlesf.model.exception.ProductNotFoundException;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
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
        ProductModelId productId = new ProductModelId("100");
        Integer newStock = 75;

        ProductModel existingProduct = ProductModel.builder()
                .id(productId)
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(new BranchModelId("10"))
                .build();

        ProductModel updatedProduct = existingProduct.toBuilder()
                .stock(newStock)
                .build();

        when(productModelRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productModelRepository.update(any(ProductModel.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(useCase.execute(productId, newStock))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("100", result.getId().value());
                    assertEquals("Hamburguesa Doble", result.getName());
                    assertEquals(75, result.getStock());
                    assertEquals("10", result.getBranchId().value());
                })
                .verifyComplete();

        verify(productModelRepository).findById(productId);
        verify(productModelRepository).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should modify product stock successfully when new stock is zero")
    void shouldModifyStockSuccessfullyWhenStockIsZero() {
        ProductModelId productId = new ProductModelId("100");
        Integer newStock = 0;

        ProductModel existingProduct = ProductModel.builder()
                .id(productId)
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(new BranchModelId("10"))
                .build();

        ProductModel updatedProduct = existingProduct.toBuilder()
                .stock(0)
                .build();

        when(productModelRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productModelRepository.update(any(ProductModel.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(useCase.execute(productId, newStock))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("100", result.getId().value());
                    assertEquals(0, result.getStock());
                })
                .verifyComplete();

        verify(productModelRepository).findById(productId);
        verify(productModelRepository).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidProductStockException when new stock is negative")
    void shouldEmitInvalidProductStockExceptionWhenStockIsNegative() {
        ProductModelId productId = new ProductModelId("100");
        Integer newStock = -5;

        StepVerifier.create(useCase.execute(productId, newStock))
                .expectErrorMatches(throwable -> throwable instanceof InvalidProductStockException
                        && throwable.getMessage().contains("greater than or equal to 0"))
                .verify();

        verify(productModelRepository, never()).findById(any(ProductModelId.class));
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidProductStockException when new stock is null")
    void shouldEmitInvalidProductStockExceptionWhenStockIsNull() {
        ProductModelId productId = new ProductModelId("100");

        StepVerifier.create(useCase.execute(productId, null))
                .expectErrorMatches(throwable -> throwable instanceof InvalidProductStockException
                        && throwable.getMessage().contains("greater than or equal to 0"))
                .verify();

        verify(productModelRepository, never()).findById(any(ProductModelId.class));
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit ProductNotFoundException when product does not exist")
    void shouldEmitProductNotFoundExceptionWhenProductDoesNotExist() {
        ProductModelId productId = new ProductModelId("999");
        Integer newStock = 20;

        when(productModelRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(productId, newStock))
                .expectErrorMatches(throwable -> throwable instanceof ProductNotFoundException
                        && throwable.getMessage().contains("999"))
                .verify();

        verify(productModelRepository).findById(productId);
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }
}
