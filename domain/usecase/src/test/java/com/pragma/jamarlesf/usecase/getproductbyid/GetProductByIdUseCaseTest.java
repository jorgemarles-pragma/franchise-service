package com.pragma.jamarlesf.usecase.getproductbyid;

import com.pragma.jamarlesf.model.constant.ErrorMessageConstants;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductByIdUseCaseTest {

    @Mock
    private ProductModelRepository productModelRepository;

    private GetProductByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetProductByIdUseCase(productModelRepository);
    }

    @Test
    @DisplayName("Should return product when product exists")
    void shouldReturnProductWhenProductExists() {
        ProductModelId productId = new ProductModelId(UseCaseTestConstants.ID_ONE_HUNDRED);
        ProductModel product = ProductModel.builder()
                .id(productId)
                .name(UseCaseTestConstants.PRODUCT_NAME_BURGER)
                .stock(UseCaseTestConstants.STOCK_FIFTY)
                .build();

        when(productModelRepository.findById(productId)).thenReturn(Mono.just(product));

        StepVerifier.create(useCase.execute(productId))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(UseCaseTestConstants.ID_ONE_HUNDRED, result.getId().value());
                    assertEquals(UseCaseTestConstants.PRODUCT_NAME_BURGER, result.getName());
                    assertEquals(UseCaseTestConstants.STOCK_FIFTY, result.getStock());
                })
                .verifyComplete();

        verify(productModelRepository).findById(productId);
    }

    @Test
    @DisplayName("Should emit ProductNotFoundException when product does not exist")
    void shouldEmitProductNotFoundExceptionWhenProductDoesNotExist() {
        ProductModelId productId = new ProductModelId(UseCaseTestConstants.ID_NON_EXISTENT);

        when(productModelRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(productId))
                .expectErrorMatches(throwable -> throwable instanceof ProductNotFoundException
                        && throwable.getMessage().contains(UseCaseTestConstants.ID_NON_EXISTENT))
                .verify();

        verify(productModelRepository).findById(productId);
    }

    @Test
    @DisplayName("Should emit ProductNotFoundException with null when product id is null")
    void shouldEmitProductNotFoundExceptionWhenProductIdIsNull() {
        when(productModelRepository.findById(null)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(null))
                .expectErrorMatches(throwable -> throwable instanceof ProductNotFoundException
                        && throwable.getMessage().contains(ErrorMessageConstants.NULL_ID_VALUE))
                .verify();

        verify(productModelRepository).findById(null);
    }
}
