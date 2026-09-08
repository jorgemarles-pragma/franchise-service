package com.pragma.jamarlesf.usecase.addproducttobranch;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.BranchNotFoundException;
import com.pragma.jamarlesf.model.exception.InvalidProductStockException;
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
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_TEN);
        BranchModel existingBranch = BranchModel.builder()
                .id(branchId)
                .name(UseCaseTestConstants.BRANCH_NAME_NORTH)
                .build();

        ProductModel inputProduct = ProductModel.builder()
                .name(UseCaseTestConstants.PRODUCT_NAME_BURGER)
                .stock(UseCaseTestConstants.STOCK_FIFTY)
                .build();

        ProductModel createdProduct = ProductModel.builder()
                .id(new ProductModelId(UseCaseTestConstants.ID_ONE_HUNDRED))
                .name(UseCaseTestConstants.PRODUCT_NAME_BURGER)
                .stock(UseCaseTestConstants.STOCK_FIFTY)
                .branchId(branchId)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(productModelRepository.create(any(ProductModel.class))).thenReturn(Mono.just(createdProduct));

        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_ONE_HUNDRED, result.getId().value());
                    assertEquals(UseCaseTestConstants.PRODUCT_NAME_BURGER, result.getName());
                    assertEquals(UseCaseTestConstants.STOCK_FIFTY, result.getStock());
                    assertEquals(UseCaseTestConstants.ID_TEN, result.getBranchId().value());
                })
                .verifyComplete();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository).create(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit BranchNotFoundException when branch does not exist")
    void shouldEmitBranchNotFoundExceptionWhenBranchDoesNotExist() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_NON_EXISTENT);
        ProductModel inputProduct = ProductModel.builder()
                .name(UseCaseTestConstants.PRODUCT_NAME_FRIES)
                .stock(UseCaseTestConstants.STOCK_TEN)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .expectErrorMatches(throwable -> throwable instanceof BranchNotFoundException
                        && throwable.getMessage().contains(UseCaseTestConstants.ID_NON_EXISTENT))
                .verify();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository, never()).create(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidProductStockException when stock is negative")
    void shouldEmitInvalidProductStockExceptionWhenStockIsNegative() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_TEN);
        ProductModel inputProduct = ProductModel.builder()
                .name(UseCaseTestConstants.PRODUCT_NAME_BURGER)
                .stock(UseCaseTestConstants.STOCK_NEGATIVE)
                .build();

        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .expectErrorMatches(throwable -> throwable instanceof InvalidProductStockException
                        && throwable.getMessage().contains(ErrorMessageConstants.PRODUCT_STOCK_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO))
                .verify();

        verify(branchModelRepository, never()).findById(any(BranchModelId.class));
        verify(productModelRepository, never()).create(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidProductStockException when stock is null")
    void shouldEmitInvalidProductStockExceptionWhenStockIsNull() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_TEN);
        ProductModel inputProduct = ProductModel.builder()
                .name(UseCaseTestConstants.PRODUCT_NAME_BURGER)
                .stock(null)
                .build();

        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .expectError(InvalidProductStockException.class)
                .verify();

        verify(branchModelRepository, never()).findById(any(BranchModelId.class));
        verify(productModelRepository, never()).create(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should add product successfully when stock is zero")
    void shouldAddProductSuccessfullyWhenStockIsZero() {
        BranchModelId branchId = new BranchModelId(UseCaseTestConstants.ID_TEN);
        BranchModel existingBranch = BranchModel.builder()
                .id(branchId)
                .name(UseCaseTestConstants.BRANCH_NAME_NORTH)
                .build();

        ProductModel inputProduct = ProductModel.builder()
                .name(UseCaseTestConstants.PRODUCT_NAME_SODA)
                .stock(UseCaseTestConstants.STOCK_ZERO)
                .build();

        ProductModel createdProduct = ProductModel.builder()
                .id(new ProductModelId(UseCaseTestConstants.ID_TWO))
                .name(UseCaseTestConstants.PRODUCT_NAME_SODA)
                .stock(UseCaseTestConstants.STOCK_ZERO)
                .branchId(branchId)
                .build();

        when(branchModelRepository.findById(branchId)).thenReturn(Mono.just(existingBranch));
        when(productModelRepository.create(any(ProductModel.class))).thenReturn(Mono.just(createdProduct));

        StepVerifier.create(useCase.execute(branchId, inputProduct))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_TWO, result.getId().value());
                    assertEquals(UseCaseTestConstants.STOCK_ZERO, result.getStock());
                })
                .verifyComplete();

        verify(branchModelRepository).findById(branchId);
        verify(productModelRepository).create(any(ProductModel.class));
    }
}
