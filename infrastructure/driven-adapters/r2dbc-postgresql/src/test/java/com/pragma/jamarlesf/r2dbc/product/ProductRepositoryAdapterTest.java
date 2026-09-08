package com.pragma.jamarlesf.r2dbc.product;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import com.pragma.jamarlesf.r2dbc.helper.ResilienceOperators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ERROR_DB_CONNECTION;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ERROR_DB_DELETE_FAILED;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ERROR_DB_QUERY_FAILED;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_NINE_NINE_NINE;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_NINE_NINE_NINE_LONG;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_NON_NUMERIC;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_ONE;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_ONE_HUNDRED;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_ONE_HUNDRED_LONG;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_ONE_HUNDRED_ONE;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_ONE_HUNDRED_ONE_LONG;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_ONE_LONG;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_TEN;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_TEN_LONG;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_TWENTY;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_TWENTY_LONG;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_TWO_HUNDRED_ONE;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_TWO_HUNDRED_ONE_LONG;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.PRODUCT_NAME_DEFAULT;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.PRODUCT_NAME_FRIES;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.STOCK_EIGHTY;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.STOCK_FIFTY;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.STOCK_FIVE;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.STOCK_SEVENTY_FIVE;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.WHITESPACE_STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    private ProductRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProductRepositoryAdapter(repository, mapper, productMapper, ResilienceOperators.defaultInstance());
    }

    @Test
    void mustCreateProductSuccessfully() {
        ProductModel inputModel = ProductModel.builder()
                .name(PRODUCT_NAME_DEFAULT)
                .stock(STOCK_FIFTY)
                .branchId(new BranchModelId(ID_TEN))
                .build();

        ProductData savedData = ProductData.builder()
                .id(ID_ONE_HUNDRED_LONG)
                .name(PRODUCT_NAME_DEFAULT)
                .stock(STOCK_FIFTY)
                .branchId(ID_TEN_LONG)
                .build();

        when(repository.save(any(ProductData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.create(inputModel))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals(ID_ONE_HUNDRED, result.getId().value());
                    assertEquals(PRODUCT_NAME_DEFAULT, result.getName());
                    assertEquals(STOCK_FIFTY, result.getStock());
                    assertNotNull(result.getBranchId());
                    assertEquals(ID_TEN, result.getBranchId().value());
                })
                .verifyComplete();

        verify(repository).save(any(ProductData.class));
    }

    @Test
    void mustPropagateErrorWhenDatabaseFails() {
        ProductModel inputModel = ProductModel.builder()
                .name(PRODUCT_NAME_DEFAULT)
                .stock(STOCK_FIVE)
                .branchId(new BranchModelId(ID_TEN))
                .build();

        RuntimeException dbException = new RuntimeException(ERROR_DB_CONNECTION);
        when(repository.save(any(ProductData.class))).thenReturn(Mono.error(dbException));

        StepVerifier.create(adapter.create(inputModel))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals(ERROR_DB_CONNECTION))
                .verify();

        verify(repository).save(any(ProductData.class));
    }

    @Test
    void mustFindProductByIdSuccessfully() {
        ProductData foundData = ProductData.builder()
                .id(ID_ONE_HUNDRED_LONG)
                .name(PRODUCT_NAME_DEFAULT)
                .stock(STOCK_FIFTY)
                .branchId(ID_TEN_LONG)
                .build();

        when(repository.findById(ID_ONE_HUNDRED_LONG)).thenReturn(Mono.just(foundData));

        StepVerifier.create(adapter.findById(new ProductModelId(ID_ONE_HUNDRED)))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(ID_ONE_HUNDRED, result.getId().value());
                    assertEquals(PRODUCT_NAME_DEFAULT, result.getName());
                    assertEquals(STOCK_FIFTY, result.getStock());
                })
                .verifyComplete();

        verify(repository).findById(ID_ONE_HUNDRED_LONG);
    }

    @Test
    void mustReturnEmptyWhenProductNotFound() {
        when(repository.findById(ID_NINE_NINE_NINE_LONG)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(new ProductModelId(ID_NINE_NINE_NINE)))
                .verifyComplete();

        verify(repository).findById(ID_NINE_NINE_NINE_LONG);
    }

    @Test
    void mustReturnEmptyWhenIdIsNull() {
        StepVerifier.create(adapter.findById((ProductModelId) null))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdIsBlank() {
        StepVerifier.create(adapter.findById(new ProductModelId(WHITESPACE_STRING)))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdIsNonNumeric() {
        StepVerifier.create(adapter.findById(new ProductModelId(ID_NON_NUMERIC)))
                .verifyComplete();
    }

    @Test
    void mustUpdateProductSuccessfully() {
        ProductModel inputModel = ProductModel.builder()
                .id(new ProductModelId(ID_ONE_HUNDRED))
                .name(PRODUCT_NAME_DEFAULT)
                .stock(STOCK_SEVENTY_FIVE)
                .branchId(new BranchModelId(ID_TEN))
                .build();

        ProductData savedData = ProductData.builder()
                .id(ID_ONE_HUNDRED_LONG)
                .name(PRODUCT_NAME_DEFAULT)
                .stock(STOCK_SEVENTY_FIVE)
                .branchId(ID_TEN_LONG)
                .build();

        when(repository.save(any(ProductData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.update(inputModel))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(ID_ONE_HUNDRED, result.getId().value());
                    assertEquals(STOCK_SEVENTY_FIVE, result.getStock());
                })
                .verifyComplete();

        verify(repository).save(any(ProductData.class));
    }

    @Test
    void mustFindHighestStockProductsSuccessfully() {
        ProductData data1 = ProductData.builder()
                .id(ID_ONE_HUNDRED_ONE_LONG)
                .name(PRODUCT_NAME_DEFAULT)
                .stock(STOCK_FIFTY)
                .branchId(ID_TEN_LONG)
                .build();

        ProductData data2 = ProductData.builder()
                .id(ID_TWO_HUNDRED_ONE_LONG)
                .name(PRODUCT_NAME_FRIES)
                .stock(STOCK_EIGHTY)
                .branchId(ID_TWENTY_LONG)
                .build();

        when(repository.findHighestStockByFranchiseId(ID_ONE_LONG)).thenReturn(Flux.just(data1, data2));

        StepVerifier.create(adapter.findHighestStockByFranchiseId(new FranchiseModelId(ID_ONE)))
                .assertNext(p1 -> {
                    assertNotNull(p1);
                    assertEquals(ID_ONE_HUNDRED_ONE, p1.getId().value());
                    assertEquals(PRODUCT_NAME_DEFAULT, p1.getName());
                    assertEquals(STOCK_FIFTY, p1.getStock());
                    assertEquals(ID_TEN, p1.getBranchId().value());
                })
                .assertNext(p2 -> {
                    assertNotNull(p2);
                    assertEquals(ID_TWO_HUNDRED_ONE, p2.getId().value());
                    assertEquals(PRODUCT_NAME_FRIES, p2.getName());
                    assertEquals(STOCK_EIGHTY, p2.getStock());
                    assertEquals(ID_TWENTY, p2.getBranchId().value());
                })
                .verifyComplete();

        verify(repository).findHighestStockByFranchiseId(ID_ONE_LONG);
    }

    @Test
    void mustFindAllProductsByBranchIdSuccessfully() {
        ProductData data1 = ProductData.builder()
                .id(ID_ONE_HUNDRED_ONE_LONG)
                .name(PRODUCT_NAME_DEFAULT)
                .stock(STOCK_FIFTY)
                .branchId(ID_TEN_LONG)
                .build();

        ProductData data2 = ProductData.builder()
                .id(ID_TWO_HUNDRED_ONE_LONG)
                .name(PRODUCT_NAME_FRIES)
                .stock(STOCK_EIGHTY)
                .branchId(ID_TEN_LONG)
                .build();

        when(repository.findAllByBranchId(ID_TEN_LONG)).thenReturn(Flux.just(data1, data2));

        StepVerifier.create(adapter.findAllByBranchId(new BranchModelId(ID_TEN)))
                .assertNext(p1 -> {
                    assertNotNull(p1);
                    assertEquals(ID_ONE_HUNDRED_ONE, p1.getId().value());
                    assertEquals(PRODUCT_NAME_DEFAULT, p1.getName());
                    assertEquals(STOCK_FIFTY, p1.getStock());
                    assertEquals(ID_TEN, p1.getBranchId().value());
                })
                .assertNext(p2 -> {
                    assertNotNull(p2);
                    assertEquals(ID_TWO_HUNDRED_ONE, p2.getId().value());
                    assertEquals(PRODUCT_NAME_FRIES, p2.getName());
                    assertEquals(STOCK_EIGHTY, p2.getStock());
                    assertEquals(ID_TEN, p2.getBranchId().value());
                })
                .verifyComplete();

        verify(repository).findAllByBranchId(ID_TEN_LONG);
    }

    @Test
    void mustReturnEmptyWhenBranchIdIsNullOnFindAllByBranchId() {
        StepVerifier.create(adapter.findAllByBranchId(null))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenBranchIdIsBlankOnFindAllByBranchId() {
        StepVerifier.create(adapter.findAllByBranchId(new BranchModelId(WHITESPACE_STRING)))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenBranchIdIsNonNumericOnFindAllByBranchId() {
        StepVerifier.create(adapter.findAllByBranchId(new BranchModelId(ID_NON_NUMERIC)))
                .verifyComplete();
    }

    @Test
    void mustPropagateErrorWhenDatabaseFailsOnFindAllByBranchId() {
        RuntimeException dbException = new RuntimeException(ERROR_DB_QUERY_FAILED);
        when(repository.findAllByBranchId(ID_TEN_LONG)).thenReturn(Flux.error(dbException));

        StepVerifier.create(adapter.findAllByBranchId(new BranchModelId(ID_TEN)))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals(ERROR_DB_QUERY_FAILED))
                .verify();

        verify(repository).findAllByBranchId(ID_TEN_LONG);
    }

    @Test
    void mustReturnEmptyWhenFranchiseIdIsNullOnFindHighestStock() {
        StepVerifier.create(adapter.findHighestStockByFranchiseId(null))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenFranchiseIdIsBlankOnFindHighestStock() {
        StepVerifier.create(adapter.findHighestStockByFranchiseId(new FranchiseModelId(WHITESPACE_STRING)))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenFranchiseIdIsNonNumericOnFindHighestStock() {
        StepVerifier.create(adapter.findHighestStockByFranchiseId(new FranchiseModelId(ID_NON_NUMERIC)))
                .verifyComplete();
    }

    @Test
    void mustPropagateErrorWhenDatabaseFailsOnFindHighestStock() {
        RuntimeException dbException = new RuntimeException(ERROR_DB_QUERY_FAILED);
        when(repository.findHighestStockByFranchiseId(ID_ONE_LONG)).thenReturn(Flux.error(dbException));

        StepVerifier.create(adapter.findHighestStockByFranchiseId(new FranchiseModelId(ID_ONE)))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals(ERROR_DB_QUERY_FAILED))
                .verify();

        verify(repository).findHighestStockByFranchiseId(ID_ONE_LONG);
    }

    @Test
    void mustDeleteProductSuccessfullyWhenIdIsValid() {
        when(repository.deleteById(ID_ONE_HUNDRED_LONG)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById(new ProductModelId(ID_ONE_HUNDRED)))
                .verifyComplete();

        verify(repository).deleteById(ID_ONE_HUNDRED_LONG);
    }

    @Test
    void mustReturnEmptyWhenIdIsNullOnDeleteById() {
        StepVerifier.create(adapter.deleteById(null))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdIsBlankOnDeleteById() {
        StepVerifier.create(adapter.deleteById(new ProductModelId(WHITESPACE_STRING)))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdIsNonNumericOnDeleteById() {
        StepVerifier.create(adapter.deleteById(new ProductModelId(ID_NON_NUMERIC)))
                .verifyComplete();
    }

    @Test
    void mustPropagateErrorWhenDatabaseFailsOnDeleteById() {
        RuntimeException dbException = new RuntimeException(ERROR_DB_DELETE_FAILED);
        when(repository.deleteById(ID_ONE_HUNDRED_LONG)).thenReturn(Mono.error(dbException));

        StepVerifier.create(adapter.deleteById(new ProductModelId(ID_ONE_HUNDRED)))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals(ERROR_DB_DELETE_FAILED))
                .verify();

        verify(repository).deleteById(ID_ONE_HUNDRED_LONG);
    }
}

