package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.ProductRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateProductNameRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateProductStockRequest;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.usecase.addproducttobranch.AddProductToBranchUseCase;
import com.pragma.jamarlesf.usecase.deleteproductfrombranch.DeleteProductFromBranchUseCase;
import com.pragma.jamarlesf.usecase.gethigheststockproductsbyfranchise.GetHighestStockProductsByFranchiseUseCase;
import com.pragma.jamarlesf.usecase.modifyproductstock.ModifyProductStockUseCase;
import com.pragma.jamarlesf.usecase.updateproductname.UpdateProductNameUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
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
    private ModifyProductStockUseCase modifyProductStockUseCase;

    @Mock
    private GetHighestStockProductsByFranchiseUseCase getHighestStockProductsByFranchiseUseCase;

    @Mock
    private UpdateProductNameUseCase updateProductNameUseCase;

    @Mock
    private DeleteProductFromBranchUseCase deleteProductFromBranchUseCase;

    @Mock
    private ServerRequest serverRequest;

    private ProductHandler productHandler;

    @BeforeEach
    void setUp() {
        productHandler = new ProductHandler(addProductToBranchUseCase, modifyProductStockUseCase, getHighestStockProductsByFranchiseUseCase, updateProductNameUseCase, deleteProductFromBranchUseCase);
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

    @Test
    @DisplayName("Should return HTTP 200 when product stock is updated successfully")
    void shouldReturn200WhenProductStockIsUpdatedSuccessfully() {
        UpdateProductStockRequest requestDto = new UpdateProductStockRequest(75);
        ProductModel updatedProduct = ProductModel.builder()
                .id(new ProductModelId("100"))
                .name("Hamburguesa Doble")
                .stock(75)
                .branchId(new BranchModelId("10"))
                .build();

        when(serverRequest.pathVariable("productId")).thenReturn("100");
        when(serverRequest.bodyToMono(UpdateProductStockRequest.class)).thenReturn(Mono.just(requestDto));
        when(modifyProductStockUseCase.execute(eq(new ProductModelId("100")), eq(75)))
                .thenReturn(Mono.just(updatedProduct));

        Mono<ServerResponse> responseMono = productHandler.updateStock(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable("productId");
        verify(serverRequest).bodyToMono(UpdateProductStockRequest.class);
        verify(modifyProductStockUseCase).execute(eq(new ProductModelId("100")), eq(75));
    }

    @Test
    @DisplayName("Should return HTTP 200 when getting highest stock products successfully")
    void shouldReturn200WhenGettingHighestStockProductsSuccessfully() {
        ProductModel product1 = ProductModel.builder()
                .id(new ProductModelId("101"))
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(new BranchModelId("10"))
                .build();

        ProductModel product2 = ProductModel.builder()
                .id(new ProductModelId("201"))
                .name("Papas Medianas")
                .stock(80)
                .branchId(new BranchModelId("20"))
                .build();

        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(getHighestStockProductsByFranchiseUseCase.execute(eq(new FranchiseModelId("1"))))
                .thenReturn(Flux.just(product1, product2));

        Mono<ServerResponse> responseMono = productHandler.getHighestStockProducts(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable("franchiseId");
        verify(getHighestStockProductsByFranchiseUseCase).execute(eq(new FranchiseModelId("1")));
    }

    @Test
    @DisplayName("Should return HTTP 200 and empty list when no products found for franchise")
    void shouldReturn200AndEmptyListWhenNoProductsFoundForFranchise() {
        when(serverRequest.pathVariable("franchiseId")).thenReturn("1");
        when(getHighestStockProductsByFranchiseUseCase.execute(eq(new FranchiseModelId("1"))))
                .thenReturn(Flux.empty());

        Mono<ServerResponse> responseMono = productHandler.getHighestStockProducts(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable("franchiseId");
        verify(getHighestStockProductsByFranchiseUseCase).execute(eq(new FranchiseModelId("1")));
    }

    @Test
    @DisplayName("Should propagate error when use case throws exception")
    void shouldPropagateErrorWhenUseCaseThrowsException() {
        when(serverRequest.pathVariable("franchiseId")).thenReturn("999");
        when(getHighestStockProductsByFranchiseUseCase.execute(eq(new FranchiseModelId("999"))))
                .thenReturn(Flux.error(new com.pragma.jamarlesf.model.exception.FranchiseNotFoundException("999")));

        Mono<ServerResponse> responseMono = productHandler.getHighestStockProducts(serverRequest);

        StepVerifier.create(responseMono)
                .expectError(com.pragma.jamarlesf.model.exception.FranchiseNotFoundException.class)
                .verify();

        verify(serverRequest).pathVariable("franchiseId");
        verify(getHighestStockProductsByFranchiseUseCase).execute(eq(new FranchiseModelId("999")));
    }

    @Test
    @DisplayName("Should return HTTP 200 when product name is updated successfully")
    void shouldReturn200WhenProductNameIsUpdatedSuccessfully() {
        UpdateProductNameRequest requestDto = new UpdateProductNameRequest("Hamburguesa Doble Carne");
        ProductModel updatedProduct = ProductModel.builder()
                .id(new ProductModelId("100"))
                .name("Hamburguesa Doble Carne")
                .stock(50)
                .branchId(new BranchModelId("10"))
                .build();

        when(serverRequest.pathVariable("productId")).thenReturn("100");
        when(serverRequest.bodyToMono(UpdateProductNameRequest.class)).thenReturn(Mono.just(requestDto));
        when(updateProductNameUseCase.execute(eq(new ProductModelId("100")), eq("Hamburguesa Doble Carne")))
                .thenReturn(Mono.just(updatedProduct));

        Mono<ServerResponse> responseMono = productHandler.updateProductName(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable("productId");
        verify(serverRequest).bodyToMono(UpdateProductNameRequest.class);
        verify(updateProductNameUseCase).execute(eq(new ProductModelId("100")), eq("Hamburguesa Doble Carne"));
    }

    @Test
    @DisplayName("Should return HTTP 204 when product is deleted successfully")
    void shouldReturn204WhenProductIsDeletedSuccessfully() {
        when(serverRequest.pathVariable("branchId")).thenReturn("10");
        when(serverRequest.pathVariable("productId")).thenReturn("100");
        when(deleteProductFromBranchUseCase.execute(eq(new BranchModelId("10")), eq(new ProductModelId("100"))))
                .thenReturn(Mono.empty());

        Mono<ServerResponse> responseMono = productHandler.deleteProduct(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.NO_CONTENT, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable("branchId");
        verify(serverRequest).pathVariable("productId");
        verify(deleteProductFromBranchUseCase).execute(eq(new BranchModelId("10")), eq(new ProductModelId("100")));
    }

    @Test
    @DisplayName("Should propagate error when delete product fails")
    void shouldPropagateErrorWhenDeleteProductFails() {
        when(serverRequest.pathVariable("branchId")).thenReturn("10");
        when(serverRequest.pathVariable("productId")).thenReturn("100");
        when(deleteProductFromBranchUseCase.execute(eq(new BranchModelId("10")), eq(new ProductModelId("100"))))
                .thenReturn(Mono.error(new RuntimeException("Product deletion failed")));

        Mono<ServerResponse> responseMono = productHandler.deleteProduct(serverRequest);

        StepVerifier.create(responseMono)
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Product deletion failed"))
                .verify();

        verify(serverRequest).pathVariable("branchId");
        verify(serverRequest).pathVariable("productId");
        verify(deleteProductFromBranchUseCase).execute(eq(new BranchModelId("10")), eq(new ProductModelId("100")));
    }
}

