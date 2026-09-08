package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.ProductRequest;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.usecase.addproducttobranch.AddProductToBranchUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    @Mock
    private AddProductToBranchUseCase addProductToBranchUseCase;

    @Mock
    private ServerRequest serverRequest;

    private ProductHandler productHandler;

    @BeforeEach
    void setUp() {
        productHandler = new ProductHandler(addProductToBranchUseCase);
    }

    @Test
    @DisplayName("Should return HTTP 201 when product is created successfully")
    void shouldReturn201WhenProductIsCreatedSuccessfully() {
        ProductRequest requestDto = new ProductRequest("Hamburguesa Doble", 50);
        ProductModel createdProduct = ProductModel.builder()
                .id(new ProductModelId("100"))
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(new BranchModelId("10"))
                .build();

        when(serverRequest.pathVariable("branchId")).thenReturn("10");
        when(serverRequest.bodyToMono(ProductRequest.class)).thenReturn(Mono.just(requestDto));
        when(addProductToBranchUseCase.execute(eq(new BranchModelId("10")), any(ProductModel.class)))
                .thenReturn(Mono.just(createdProduct));

        Mono<ServerResponse> responseMono = productHandler.addProduct(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.CREATED, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable("branchId");
        verify(serverRequest).bodyToMono(ProductRequest.class);
        verify(addProductToBranchUseCase).execute(eq(new BranchModelId("10")), any(ProductModel.class));
    }
}
