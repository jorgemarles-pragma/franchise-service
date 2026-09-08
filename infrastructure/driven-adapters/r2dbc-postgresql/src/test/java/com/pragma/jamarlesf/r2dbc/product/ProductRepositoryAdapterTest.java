package com.pragma.jamarlesf.r2dbc.product;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
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
        adapter = new ProductRepositoryAdapter(repository, mapper, productMapper);
    }

    @Test
    void mustCreateProductSuccessfully() {
        ProductModel inputModel = ProductModel.builder()
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(new BranchModelId("10"))
                .build();

        ProductData savedData = ProductData.builder()
                .id(100L)
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(10L)
                .build();

        when(repository.save(any(ProductData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.create(inputModel))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals("100", result.getId().value());
                    assertEquals("Hamburguesa Doble", result.getName());
                    assertEquals(50, result.getStock());
                    assertNotNull(result.getBranchId());
                    assertEquals("10", result.getBranchId().value());
                })
                .verifyComplete();

        verify(repository).save(any(ProductData.class));
    }

    @Test
    void mustPropagateErrorWhenDatabaseFails() {
        ProductModel inputModel = ProductModel.builder()
                .name("Producto Fallido")
                .stock(5)
                .branchId(new BranchModelId("10"))
                .build();

        RuntimeException dbException = new RuntimeException("Database connection error");
        when(repository.save(any(ProductData.class))).thenReturn(Mono.error(dbException));

        StepVerifier.create(adapter.create(inputModel))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Database connection error"))
                .verify();

        verify(repository).save(any(ProductData.class));
    }

    @Test
    void mustFindProductByIdSuccessfully() {
        ProductData foundData = ProductData.builder()
                .id(100L)
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(10L)
                .build();

        when(repository.findById(100L)).thenReturn(Mono.just(foundData));

        StepVerifier.create(adapter.findById(new com.pragma.jamarlesf.model.productmodel.ProductModelId("100")))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("100", result.getId().value());
                    assertEquals("Hamburguesa Doble", result.getName());
                    assertEquals(50, result.getStock());
                })
                .verifyComplete();

        verify(repository).findById(100L);
    }

    @Test
    void mustReturnEmptyWhenProductNotFound() {
        when(repository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(new com.pragma.jamarlesf.model.productmodel.ProductModelId("999")))
                .verifyComplete();

        verify(repository).findById(999L);
    }

    @Test
    void mustReturnEmptyWhenIdIsNull() {
        StepVerifier.create(adapter.findById((com.pragma.jamarlesf.model.productmodel.ProductModelId) null))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdIsBlank() {
        StepVerifier.create(adapter.findById(new com.pragma.jamarlesf.model.productmodel.ProductModelId("  ")))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdIsNonNumeric() {
        StepVerifier.create(adapter.findById(new com.pragma.jamarlesf.model.productmodel.ProductModelId("abc")))
                .verifyComplete();
    }

    @Test
    void mustUpdateProductSuccessfully() {
        ProductModel inputModel = ProductModel.builder()
                .id(new com.pragma.jamarlesf.model.productmodel.ProductModelId("100"))
                .name("Hamburguesa Doble")
                .stock(75)
                .branchId(new BranchModelId("10"))
                .build();

        ProductData savedData = ProductData.builder()
                .id(100L)
                .name("Hamburguesa Doble")
                .stock(75)
                .branchId(10L)
                .build();

        when(repository.save(any(ProductData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.update(inputModel))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals("100", result.getId().value());
                    assertEquals(75, result.getStock());
                })
                .verifyComplete();

        verify(repository).save(any(ProductData.class));
    }

    @Test
    void mustFindHighestStockProductsSuccessfully() {
        ProductData data1 = ProductData.builder()
                .id(101L)
                .name("Hamburguesa Doble")
                .stock(50)
                .branchId(10L)
                .build();

        ProductData data2 = ProductData.builder()
                .id(201L)
                .name("Papas Medianas")
                .stock(80)
                .branchId(20L)
                .build();

        when(repository.findHighestStockByFranchiseId(1L)).thenReturn(Flux.just(data1, data2));

        StepVerifier.create(adapter.findHighestStockByFranchiseId(new FranchiseModelId("1")))
                .assertNext(p1 -> {
                    assertNotNull(p1);
                    assertEquals("101", p1.getId().value());
                    assertEquals("Hamburguesa Doble", p1.getName());
                    assertEquals(50, p1.getStock());
                    assertEquals("10", p1.getBranchId().value());
                })
                .assertNext(p2 -> {
                    assertNotNull(p2);
                    assertEquals("201", p2.getId().value());
                    assertEquals("Papas Medianas", p2.getName());
                    assertEquals(80, p2.getStock());
                    assertEquals("20", p2.getBranchId().value());
                })
                .verifyComplete();

        verify(repository).findHighestStockByFranchiseId(1L);
    }

    @Test
    void mustReturnEmptyWhenFranchiseIdIsNullOnFindHighestStock() {
        StepVerifier.create(adapter.findHighestStockByFranchiseId(null))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenFranchiseIdIsBlankOnFindHighestStock() {
        StepVerifier.create(adapter.findHighestStockByFranchiseId(new FranchiseModelId("  ")))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenFranchiseIdIsNonNumericOnFindHighestStock() {
        StepVerifier.create(adapter.findHighestStockByFranchiseId(new FranchiseModelId("abc")))
                .verifyComplete();
    }

    @Test
    void mustPropagateErrorWhenDatabaseFailsOnFindHighestStock() {
        RuntimeException dbException = new RuntimeException("DB query failed");
        when(repository.findHighestStockByFranchiseId(1L)).thenReturn(Flux.error(dbException));

        StepVerifier.create(adapter.findHighestStockByFranchiseId(new FranchiseModelId("1")))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("DB query failed"))
                .verify();

        verify(repository).findHighestStockByFranchiseId(1L);
    }
}

