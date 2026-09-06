package com.pragma.jamarlesf.r2dbc.franchise;

import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
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
class FranchiseRepositoryAdapterTest {

    @Mock
    private FranchiseReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private final FranchiseMapper franchiseMapper = Mappers.getMapper(FranchiseMapper.class);

    private FranchiseRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FranchiseRepositoryAdapter(repository, mapper, franchiseMapper);
    }

    @Test
    void mustCreateFranchiseSuccessfully() {
        FranchiseModel inputModel = FranchiseModel.builder()
                .name("Franquicia Nequi")
                .build();

        FranchiseData savedData = FranchiseData.builder()
                .id(1L)
                .name("Franquicia Nequi")
                .build();

        when(repository.save(any(FranchiseData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.create(inputModel))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals("1", result.getId().value());
                    assertEquals("Franquicia Nequi", result.getName());
                })
                .verifyComplete();

        verify(repository).save(any(FranchiseData.class));
    }

    @Test
    void mustPropagateErrorWhenDatabaseFails() {
        FranchiseModel inputModel = FranchiseModel.builder()
                .name("Franquicia Fallida")
                .build();

        RuntimeException dbException = new RuntimeException("Database connection timeout");
        when(repository.save(any(FranchiseData.class))).thenReturn(Mono.error(dbException));

        StepVerifier.create(adapter.create(inputModel))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Database connection timeout"))
                .verify();

        verify(repository).save(any(FranchiseData.class));
    }
}
