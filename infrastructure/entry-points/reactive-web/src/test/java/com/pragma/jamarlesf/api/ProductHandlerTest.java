package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.constant.ApiTestConstants;
import com.pragma.jamarlesf.api.dto.request.ProductRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateProductNameRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateProductStockRequest;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.exception.FranchiseNotFoundException;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.usecase.addproducttobranch.AddProductToBranchUseCase;
import com.pragma.jamarlesf.usecase.deleteproductfrombranch.DeleteProductFromBranchUseCase;
import com.pragma.jamarlesf.usecase.gethigheststockproductsbyfranchise.GetHighestStockProductsByFranchiseUseCase;
import com.pragma.jamarlesf.usecase.getproductbyid.GetProductByIdUseCase;
import com.pragma.jamarlesf.usecase.getproductsbybranch.GetProductsByBranchUseCase;
import com.pragma.jamarlesf.usecase.modifyproductstock.ModifyProductStockUseCase;
import com.pragma.jamarlesf.usecase.updateproductname.UpdateProductNameUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_BRANCH_ID;
import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_FRANCHISE_ID;
import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_PRODUCT_ID;
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
    private GetProductByIdUseCase getProductByIdUseCase;

    @Mock
    private GetProductsByBranchUseCase getProductsByBranchUseCase;

    @Mock
    private ServerRequest serverRequest;

    private ProductHandler productHandler;

    @BeforeEach
    void setUp() {
        productHandler = new ProductHandler(
                addProductToBranchUseCase,
                modifyProductStockUseCase,
                getHighestStockProductsByFranchiseUseCase,
                updateProductNameUseCase,
                deleteProductFromBranchUseCase,
                getProductByIdUseCase,
                getProductsByBranchUseCase
        );
    }

    @Test
    @DisplayName("Should return HTTP 201 when product is created successfully")
    void shouldReturn201WhenProductIsCreatedSuccessfully() {
        ProductRequest requestDto = new ProductRequest(ApiTestConstants.PRODUCT_NAME_DEFAULT, ApiTestConstants.STOCK_FIFTY);
        ProductModel createdProduct = ProductModel.builder()
                .id(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED))
                .name(ApiTestConstants.PRODUCT_NAME_DEFAULT)
                .stock(ApiTestConstants.STOCK_FIFTY)
                .branchId(new BranchModelId(ApiTestConstants.ID_TEN))
                .build();

        when(serverRequest.pathVariable(PATH_VAR_BRANCH_ID)).thenReturn(ApiTestConstants.ID_TEN);
        when(serverRequest.bodyToMono(ProductRequest.class)).thenReturn(Mono.just(requestDto));
        when(addProductToBranchUseCase.execute(eq(new BranchModelId(ApiTestConstants.ID_TEN)), any(ProductModel.class)))
                .thenReturn(Mono.just(createdProduct));

        Mono<ServerResponse> responseMono = productHandler.addProduct(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.CREATED, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_BRANCH_ID);
        verify(serverRequest).bodyToMono(ProductRequest.class);
        verify(addProductToBranchUseCase).execute(eq(new BranchModelId(ApiTestConstants.ID_TEN)), any(ProductModel.class));
    }

    @Test
    @DisplayName("Should return HTTP 200 when product stock is updated successfully")
    void shouldReturn200WhenProductStockIsUpdatedSuccessfully() {
        UpdateProductStockRequest requestDto = new UpdateProductStockRequest(ApiTestConstants.STOCK_SEVENTY_FIVE);
        ProductModel updatedProduct = ProductModel.builder()
                .id(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED))
                .name(ApiTestConstants.PRODUCT_NAME_DEFAULT)
                .stock(ApiTestConstants.STOCK_SEVENTY_FIVE)
                .branchId(new BranchModelId(ApiTestConstants.ID_TEN))
                .build();

        when(serverRequest.pathVariable(PATH_VAR_PRODUCT_ID)).thenReturn(ApiTestConstants.ID_ONE_HUNDRED);
        when(serverRequest.bodyToMono(UpdateProductStockRequest.class)).thenReturn(Mono.just(requestDto));
        when(modifyProductStockUseCase.execute(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED), ApiTestConstants.STOCK_SEVENTY_FIVE))
                .thenReturn(Mono.just(updatedProduct));

        Mono<ServerResponse> responseMono = productHandler.updateStock(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_PRODUCT_ID);
        verify(serverRequest).bodyToMono(UpdateProductStockRequest.class);
        verify(modifyProductStockUseCase).execute(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED), ApiTestConstants.STOCK_SEVENTY_FIVE);
    }

    @Test
    @DisplayName("Should return HTTP 200 when getting highest stock products successfully")
    void shouldReturn200WhenGettingHighestStockProductsSuccessfully() {
        ProductModel product1 = ProductModel.builder()
                .id(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED))
                .name(ApiTestConstants.PRODUCT_NAME_DEFAULT)
                .stock(ApiTestConstants.STOCK_FIFTY)
                .branchId(new BranchModelId(ApiTestConstants.ID_TEN))
                .build();

        ProductModel product2 = ProductModel.builder()
                .id(new ProductModelId(ApiTestConstants.ID_TWO))
                .name(ApiTestConstants.PRODUCT_NAME_FRIES)
                .stock(ApiTestConstants.STOCK_EIGHTY)
                .branchId(new BranchModelId(ApiTestConstants.ID_TWENTY))
                .build();

        when(serverRequest.pathVariable(PATH_VAR_FRANCHISE_ID)).thenReturn(ApiTestConstants.ID_ONE);
        when(getHighestStockProductsByFranchiseUseCase.execute(new FranchiseModelId(ApiTestConstants.ID_ONE)))
                .thenReturn(Flux.just(product1, product2));

        Mono<ServerResponse> responseMono = productHandler.getHighestStockProducts(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                    assertEquals(MediaType.APPLICATION_NDJSON, response.headers().getContentType());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_FRANCHISE_ID);
        verify(getHighestStockProductsByFranchiseUseCase).execute(new FranchiseModelId(ApiTestConstants.ID_ONE));
    }

    @Test
    @DisplayName("Should return HTTP 200 and stream when no products found for franchise")
    void shouldReturn200AndStreamWhenNoProductsFoundForFranchise() {
        when(serverRequest.pathVariable(PATH_VAR_FRANCHISE_ID)).thenReturn(ApiTestConstants.ID_ONE);
        when(getHighestStockProductsByFranchiseUseCase.execute(new FranchiseModelId(ApiTestConstants.ID_ONE)))
                .thenReturn(Flux.empty());

        Mono<ServerResponse> responseMono = productHandler.getHighestStockProducts(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                    assertEquals(MediaType.APPLICATION_NDJSON, response.headers().getContentType());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_FRANCHISE_ID);
        verify(getHighestStockProductsByFranchiseUseCase).execute(new FranchiseModelId(ApiTestConstants.ID_ONE));
    }

    @Test
    @DisplayName("Should return ServerResponse wrapping Flux that emits error when use case throws exception")
    void shouldPropagateErrorWhenUseCaseThrowsException() {
        when(serverRequest.pathVariable(PATH_VAR_FRANCHISE_ID)).thenReturn(ApiTestConstants.ID_NON_EXISTENT);
        when(getHighestStockProductsByFranchiseUseCase.execute(new FranchiseModelId(ApiTestConstants.ID_NON_EXISTENT)))
                .thenReturn(Flux.error(new FranchiseNotFoundException(ApiTestConstants.ID_NON_EXISTENT)));

        Mono<ServerResponse> responseMono = productHandler.getHighestStockProducts(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                    assertEquals(MediaType.APPLICATION_NDJSON, response.headers().getContentType());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_FRANCHISE_ID);
        verify(getHighestStockProductsByFranchiseUseCase).execute(new FranchiseModelId(ApiTestConstants.ID_NON_EXISTENT));
    }

    @Test
    @DisplayName("Should return HTTP 200 when product name is updated successfully")
    void shouldReturn200WhenProductNameIsUpdatedSuccessfully() {
        UpdateProductNameRequest requestDto = new UpdateProductNameRequest(ApiTestConstants.PRODUCT_NAME_UPDATED);
        ProductModel updatedProduct = ProductModel.builder()
                .id(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED))
                .name(ApiTestConstants.PRODUCT_NAME_UPDATED)
                .stock(ApiTestConstants.STOCK_FIFTY)
                .branchId(new BranchModelId(ApiTestConstants.ID_TEN))
                .build();

        when(serverRequest.pathVariable(PATH_VAR_PRODUCT_ID)).thenReturn(ApiTestConstants.ID_ONE_HUNDRED);
        when(serverRequest.bodyToMono(UpdateProductNameRequest.class)).thenReturn(Mono.just(requestDto));
        when(updateProductNameUseCase.execute(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED), ApiTestConstants.PRODUCT_NAME_UPDATED))
                .thenReturn(Mono.just(updatedProduct));

        Mono<ServerResponse> responseMono = productHandler.updateProductName(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_PRODUCT_ID);
        verify(serverRequest).bodyToMono(UpdateProductNameRequest.class);
        verify(updateProductNameUseCase).execute(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED), ApiTestConstants.PRODUCT_NAME_UPDATED);
    }

    @Test
    @DisplayName("Should return HTTP 204 when product is deleted successfully")
    void shouldReturn204WhenProductIsDeletedSuccessfully() {
        when(serverRequest.pathVariable(PATH_VAR_BRANCH_ID)).thenReturn(ApiTestConstants.ID_TEN);
        when(serverRequest.pathVariable(PATH_VAR_PRODUCT_ID)).thenReturn(ApiTestConstants.ID_ONE_HUNDRED);
        when(deleteProductFromBranchUseCase.execute(new BranchModelId(ApiTestConstants.ID_TEN), new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED)))
                .thenReturn(Mono.empty());

        Mono<ServerResponse> responseMono = productHandler.deleteProduct(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.NO_CONTENT, response.statusCode());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_BRANCH_ID);
        verify(serverRequest).pathVariable(PATH_VAR_PRODUCT_ID);
        verify(deleteProductFromBranchUseCase).execute(new BranchModelId(ApiTestConstants.ID_TEN), new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED));
    }

    @Test
    @DisplayName("Should propagate error when delete product fails")
    void shouldPropagateErrorWhenDeleteProductFails() {
        when(serverRequest.pathVariable(PATH_VAR_BRANCH_ID)).thenReturn(ApiTestConstants.ID_TEN);
        when(serverRequest.pathVariable(PATH_VAR_PRODUCT_ID)).thenReturn(ApiTestConstants.ID_ONE_HUNDRED);
        when(deleteProductFromBranchUseCase.execute(new BranchModelId(ApiTestConstants.ID_TEN), new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED)))
                .thenReturn(Mono.error(new RuntimeException(ApiTestConstants.SAMPLE_ERROR_MESSAGE)));

        Mono<ServerResponse> responseMono = productHandler.deleteProduct(serverRequest);

        StepVerifier.create(responseMono)
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals(ApiTestConstants.SAMPLE_ERROR_MESSAGE))
                .verify();

        verify(serverRequest).pathVariable(PATH_VAR_BRANCH_ID);
        verify(serverRequest).pathVariable(PATH_VAR_PRODUCT_ID);
        verify(deleteProductFromBranchUseCase).execute(new BranchModelId(ApiTestConstants.ID_TEN), new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED));
    }

    @Test
    @DisplayName("Should return HTTP 200 when getting product by id successfully")
    void shouldReturn200WhenGettingProductByIdSuccessfully() {
        ProductModel product = ProductModel.builder()
                .id(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED))
                .name(ApiTestConstants.PRODUCT_NAME_DEFAULT)
                .stock(ApiTestConstants.STOCK_FIFTY)
                .branchId(new BranchModelId(ApiTestConstants.ID_TEN))
                .build();

        when(serverRequest.pathVariable(PATH_VAR_PRODUCT_ID)).thenReturn(ApiTestConstants.ID_ONE_HUNDRED);
        when(getProductByIdUseCase.execute(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED)))
                .thenReturn(Mono.just(product));

        Mono<ServerResponse> responseMono = productHandler.getProductById(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.headers().getContentType());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_PRODUCT_ID);
        verify(getProductByIdUseCase).execute(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED));
    }

    @Test
    @DisplayName("Should return HTTP 200 and stream NDJSON when getting products by branch successfully")
    void shouldReturn200AndStreamNDJSONWhenGettingProductsByBranchSuccessfully() {
        ProductModel product1 = ProductModel.builder()
                .id(new ProductModelId(ApiTestConstants.ID_ONE_HUNDRED))
                .name(ApiTestConstants.PRODUCT_NAME_DEFAULT)
                .stock(ApiTestConstants.STOCK_FIFTY)
                .branchId(new BranchModelId(ApiTestConstants.ID_TEN))
                .build();

        ProductModel product2 = ProductModel.builder()
                .id(new ProductModelId(ApiTestConstants.ID_TWO))
                .name(ApiTestConstants.PRODUCT_NAME_FRIES)
                .stock(ApiTestConstants.STOCK_EIGHTY)
                .branchId(new BranchModelId(ApiTestConstants.ID_TEN))
                .build();

        when(serverRequest.pathVariable(PATH_VAR_BRANCH_ID)).thenReturn(ApiTestConstants.ID_TEN);
        when(getProductsByBranchUseCase.execute(new BranchModelId(ApiTestConstants.ID_TEN)))
                .thenReturn(Flux.just(product1, product2));

        Mono<ServerResponse> responseMono = productHandler.getProductsByBranch(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(HttpStatus.OK, response.statusCode());
                    assertEquals(MediaType.APPLICATION_NDJSON, response.headers().getContentType());
                })
                .verifyComplete();

        verify(serverRequest).pathVariable(PATH_VAR_BRANCH_ID);
        verify(getProductsByBranchUseCase).execute(new BranchModelId(ApiTestConstants.ID_TEN));
    }
}
