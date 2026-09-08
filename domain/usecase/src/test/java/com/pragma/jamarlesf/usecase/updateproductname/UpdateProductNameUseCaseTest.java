package com.pragma.jamarlesf.usecase.updateproductname;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
import com.pragma.jamarlesf.model.exception.InvalidProductNameException;
import com.pragma.jamarlesf.model.exception.ProductNotFoundException;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.model.productmodel.gateways.ProductModelRepository;
import com.pragma.jamarlesf.usecase.constant.UseCaseTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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
class UpdateProductNameUseCaseTest {

    @Mock
    private ProductModelRepository productModelRepository;

    private UpdateProductNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProductNameUseCase(productModelRepository);
    }

    @Test
    @DisplayName("Should update product name successfully when product exists and new name is valid")
    void shouldUpdateProductNameSuccessfullyWhenProductExistsAndNameIsValid() {
        ProductModelId productId = new ProductModelId(UseCaseTestConstants.ID_ONE_HUNDRED);
        String newName = UseCaseTestConstants.PRODUCT_NAME_WITH_SPACES;

        ProductModel existingProduct = ProductModel.builder()
                .id(productId)
                .name(UseCaseTestConstants.PRODUCT_NAME_OLD)
                .stock(UseCaseTestConstants.STOCK_FIFTY)
                .branchId(new BranchModelId(UseCaseTestConstants.ID_TEN))
                .build();

        ProductModel updatedProduct = existingProduct.toBuilder()
                .name(UseCaseTestConstants.PRODUCT_NAME_NEW)
                .build();

        when(productModelRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productModelRepository.update(any(ProductModel.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(useCase.execute(productId, newName))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_ONE_HUNDRED, result.getId().value());
                    assertEquals(UseCaseTestConstants.PRODUCT_NAME_NEW, result.getName());
                    assertEquals(UseCaseTestConstants.STOCK_FIFTY, result.getStock());
                    assertEquals(UseCaseTestConstants.ID_TEN, result.getBranchId().value());
                })
                .verifyComplete();

        verify(productModelRepository).findById(productId);
        verify(productModelRepository).update(any(ProductModel.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {UseCaseTestConstants.WHITESPACE_STRING, "\t\n"})
    @DisplayName("Should emit InvalidProductNameException when new name is null, empty, or blank")
    void shouldEmitInvalidProductNameExceptionWhenNameIsInvalid(String invalidName) {
        ProductModelId productId = new ProductModelId(UseCaseTestConstants.ID_ONE_HUNDRED);

        StepVerifier.create(useCase.execute(productId, invalidName))
                .expectErrorMatches(throwable -> throwable instanceof InvalidProductNameException
                        && throwable.getMessage().contains(ErrorMessageConstants.PRODUCT_NAME_CANNOT_BE_EMPTY_OR_NULL))
                .verify();

        verify(productModelRepository, never()).findById(any(ProductModelId.class));
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit ProductNotFoundException when product does not exist")
    void shouldEmitProductNotFoundExceptionWhenProductDoesNotExist() {
        ProductModelId productId = new ProductModelId(UseCaseTestConstants.ID_NON_EXISTENT);
        String newName = UseCaseTestConstants.PRODUCT_NAME_NEW;

        when(productModelRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(productId, newName))
                .expectErrorMatches(throwable -> throwable instanceof ProductNotFoundException
                        && throwable.getMessage().contains(UseCaseTestConstants.ID_NON_EXISTENT))
                .verify();

        verify(productModelRepository).findById(productId);
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }
}
