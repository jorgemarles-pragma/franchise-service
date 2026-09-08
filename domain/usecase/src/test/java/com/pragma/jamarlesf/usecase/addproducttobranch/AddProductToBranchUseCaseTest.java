package com.pragma.jamarlesf.usecase.addproducttobranch;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.exception.InvalidProductStockException;
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
class AddProductToBranchUseCaseTest {

    @Mock
    private BranchModelRepository branchModelRepository;

    @Mock
    private ProductModelRepository productModelRepository;

    private AddProductToBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddProductToBranchUseCase(branchModelRepository, productModelRepository);
    }

    @Test
    @DisplayName("Should add product successfully when branch exists")
    void shouldAddProductSuccessfullyWhenBranchExists() {
        BranchModelId branchId = new BranchModelId("10");
        BranchModel existingBranch = BranchModel.builder()
                .id(branchId)
                .name("Sucursal Norte")
                .build();

        ProductModel inputProduct = ProductModel.builder()
                .name("Hamburguesa Doble")
                .stock(50)
                .build();

        ProductModel createdProduct = ProductModel.builder()
                .id(new ProductModelId("100"))
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(branchId)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(productModelRepository.create(any(ProductModel.class))).thenReturn(Mono.just(createdProduct));

        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("100", result.getId().value());
                    assertEquals("Hamburguesa Doble", result.getName());
                    assertEquals(50, result.getStock());
                    assertEquals("10", result.getBranchId().value());
                })
                .verifyComplete();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository).create(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit BranchNotFoundException when branch does not exist")
    void shouldEmitBranchNotFoundExceptionWhenBranchDoesNotExist() {
        BranchModelId branchId = new BranchModelId("999");
        ProductModel inputProduct = ProductModel.builder()
                .name("Producto Huérfano")
                .stock(10)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .expectErrorMatches(throwable -> throwable instanceof BranchNotFoundException
                        && throwable.getMessage().contains("999"))
                .verify();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository, never()).create(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidProductStockException when stock is negative")
    void shouldEmitInvalidProductStockExceptionWhenStockIsNegative() {
        BranchModelId branchId = new BranchModelId("10");
        ProductModel inputProduct = ProductModel.builder()
                .name("Hamburguesa Doble")
                .stock(-1)
                .build();

        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .expectErrorMatches(throwable -> throwable instanceof InvalidProductStockException
                        && throwable.getMessage().contains("greater than or equal to 0"))
                .verify();

        verify(branchModelRepository, never()).findById(any(BranchModelId.class));
        verify(productModelRepository, never()).create(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidProductStockException when stock is null")
    void shouldEmitInvalidProductStockExceptionWhenStockIsNull() {
        BranchModelId branchId = new BranchModelId("10");
        ProductModel inputProduct = ProductModel.builder()
                .name("Hamburguesa Doble")
                .stock(null)
                .build();

        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .expectErrorMatches(throwable -> throwable instanceof InvalidProductStockException)
                .verify();

        verify(branchModelRepository, never()).findById(any(BranchModelId.class));
        verify(productModelRepository, never()).create(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should add product successfully when stock is zero")
    void shouldAddProductSuccessfullyWhenStockIsZero() {
        BranchModelId branchId = new BranchModelId("10");
        BranchModel existingBranch = BranchModel.builder()
                .id(branchId)
                .name("Sucursal Norte")
                .build();

        ProductModel inputProduct = ProductModel.builder()
                .name("Producto Sin Stock Inicial")
                .stock(0)
                .build();

        ProductModel createdProduct = ProductModel.builder()
                .id(new ProductModelId("101"))
                .name("Producto Sin Stock Inicial")
                .stock(0)
                .branchId(branchId)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(productModelRepository.create(any(ProductModel.class))).thenReturn(Mono.just(createdProduct));

        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("101", result.getId().value());
                    assertEquals(0, result.getStock());
                })
                .verifyComplete();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository).create(any(ProductModel.class));
    }
}
