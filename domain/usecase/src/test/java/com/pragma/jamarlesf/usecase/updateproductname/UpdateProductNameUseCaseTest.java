package com.pragma.jamarlesf.usecase.updateproductname;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.exception.InvalidProductNameException;
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
        ProductModelId productId = new ProductModelId("100");
        String newName = "  Hamburguesa Doble Carne  ";

        ProductModel existingProduct = ProductModel.builder()
                .id(productId)
                .name("Hamburguesa Simple")
                .stock(50)
                .branchId(new BranchModelId("10"))
                .build();

        ProductModel updatedProduct = existingProduct.toBuilder()
                .name("Hamburguesa Doble Carne")
                .build();

        when(productModelRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productModelRepository.update(any(ProductModel.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(useCase.execute(productId, newName))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("100", result.getId().value());
                    assertEquals("Hamburguesa Doble Carne", result.getName());
                    assertEquals(50, result.getStock());
                    assertEquals("10", result.getBranchId().value());
                })
                .verifyComplete();

        verify(productModelRepository).findById(productId);
        verify(productModelRepository).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidProductNameException when new name is null")
    void shouldEmitInvalidProductNameExceptionWhenNameIsNull() {
        ProductModelId productId = new ProductModelId("100");

        StepVerifier.create(useCase.execute(productId, null))
                .expectErrorMatches(throwable -> throwable instanceof InvalidProductNameException
                        && throwable.getMessage().contains("Product name cannot be empty or null"))
                .verify();

        verify(productModelRepository, never()).findById(any(ProductModelId.class));
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidProductNameException when new name is empty")
    void shouldEmitInvalidProductNameExceptionWhenNameIsEmpty() {
        ProductModelId productId = new ProductModelId("100");

        StepVerifier.create(useCase.execute(productId, ""))
                .expectErrorMatches(throwable -> throwable instanceof InvalidProductNameException
                        && throwable.getMessage().contains("Product name cannot be empty or null"))
                .verify();

        verify(productModelRepository, never()).findById(any(ProductModelId.class));
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit InvalidProductNameException when new name is whitespace only")
    void shouldEmitInvalidProductNameExceptionWhenNameIsWhitespaceOnly() {
        ProductModelId productId = new ProductModelId("100");

        StepVerifier.create(useCase.execute(productId, "   "))
                .expectErrorMatches(throwable -> throwable instanceof InvalidProductNameException
                        && throwable.getMessage().contains("Product name cannot be empty or null"))
                .verify();

        verify(productModelRepository, never()).findById(any(ProductModelId.class));
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }

    @Test
    @DisplayName("Should emit ProductNotFoundException when product does not exist")
    void shouldEmitProductNotFoundExceptionWhenProductDoesNotExist() {
        ProductModelId productId = new ProductModelId("999");
        String newName = "Hamburguesa Doble Carne";

        when(productModelRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(productId, newName))
                .expectErrorMatches(throwable -> throwable instanceof ProductNotFoundException
                        && throwable.getMessage().contains("999"))
                .verify();

        verify(productModelRepository).findById(productId);
        verify(productModelRepository, never()).update(any(ProductModel.class));
    }
}
