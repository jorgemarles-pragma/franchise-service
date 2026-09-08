package com.pragma.jamarlesf.r2dbc.branch;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
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
class BranchRepositoryAdapterTest {

    @Mock
    private BranchReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private final BranchMapper branchMapper = Mappers.getMapper(BranchMapper.class);

    private BranchRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BranchRepositoryAdapter(repository, mapper, branchMapper);
    }

    @Test
    void mustCreateBranchSuccessfully() {
        BranchModel inputModel = BranchModel.builder()
                .name("Sucursal Norte")
                .franchiseId(new FranchiseModelId("1"))
                .build();

        BranchData savedData = BranchData.builder()
                .id(10L)
                .name("Sucursal Norte")
                .franchiseId(1L)
                .build();

        when(repository.save(any(BranchData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.create(inputModel))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals("10", result.getId().value());
                    assertEquals("Sucursal Norte", result.getName());
                    assertNotNull(result.getFranchiseId());
                    assertEquals("1", result.getFranchiseId().value());
                })
                .verifyComplete();

        verify(repository).save(any(BranchData.class));
    }

    @Test
    void mustPropagateErrorWhenDatabaseFails() {
        BranchModel inputModel = BranchModel.builder()
                .name("Sucursal Fallida")
                .franchiseId(new FranchiseModelId("1"))
                .build();

        RuntimeException dbException = new RuntimeException("Database connection timeout");
        when(repository.save(any(BranchData.class))).thenReturn(Mono.error(dbException));

        StepVerifier.create(adapter.create(inputModel))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Database connection timeout"))
                .verify();

        verify(repository).save(any(BranchData.class));
    }
}
