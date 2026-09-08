package com.pragma.jamarlesf.r2dbc.product;

import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
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
}
