package com.pragma.jamarlesf.r2dbc.franchise;

import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants;
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
        adapter = new FranchiseRepositoryAdapter(repository, mapper, franchiseMapper, ResilienceOperators.defaultInstance());
    }

    @Test
    void mustCreateFranchiseSuccessfully() {
        FranchiseModel inputModel = FranchiseModel.builder()
                .name(R2dbcTestConstants.FRANCHISE_NAME_DEFAULT)
                .build();

        FranchiseData savedData = FranchiseData.builder()
                .id(R2dbcTestConstants.ID_ONE_LONG)
                .name(R2dbcTestConstants.FRANCHISE_NAME_DEFAULT)
                .build();

        when(repository.save(any(FranchiseData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.create(inputModel))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals(R2dbcTestConstants.ID_ONE, result.getId().value());
                    assertEquals(R2dbcTestConstants.FRANCHISE_NAME_DEFAULT, result.getName());
                })
                .verifyComplete();

        verify(repository).save(any(FranchiseData.class));
    }

    @Test
    void mustPropagateErrorWhenDatabaseFails() {
        FranchiseModel inputModel = FranchiseModel.builder()
                .name(R2dbcTestConstants.FRANCHISE_NAME_DEFAULT)
                .build();

        RuntimeException dbException = new RuntimeException("Database error");
        when(repository.save(any(FranchiseData.class))).thenReturn(Mono.error(dbException));

        StepVerifier.create(adapter.create(inputModel))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Database error"))
                .verify();

        verify(repository).save(any(FranchiseData.class));
    }

    @Test
    void mustFindFranchiseByIdSuccessfully() {
        FranchiseData foundData = FranchiseData.builder()
                .id(R2dbcTestConstants.ID_ONE_LONG)
                .name(R2dbcTestConstants.FRANCHISE_NAME_DEFAULT)
                .build();

        when(repository.findById(R2dbcTestConstants.ID_ONE_LONG)).thenReturn(Mono.just(foundData));

        StepVerifier.create(adapter.findById(new FranchiseModelId(R2dbcTestConstants.ID_ONE)))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(R2dbcTestConstants.ID_ONE, result.getId().value());
                    assertEquals(R2dbcTestConstants.FRANCHISE_NAME_DEFAULT, result.getName());
                })
                .verifyComplete();

        verify(repository).findById(R2dbcTestConstants.ID_ONE_LONG);
    }

    @Test
    void mustReturnEmptyWhenFranchiseNotFound() {
        when(repository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(new FranchiseModelId("999")))
                .verifyComplete();

        verify(repository).findById(999L);
    }

    @Test
    void mustReturnEmptyWhenIdIsNull() {
        StepVerifier.create(adapter.findById((FranchiseModelId) null))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdIsBlank() {
        StepVerifier.create(adapter.findById(new FranchiseModelId(R2dbcTestConstants.WHITESPACE_STRING)))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdIsNonNumeric() {
        StepVerifier.create(adapter.findById(new FranchiseModelId(R2dbcTestConstants.ID_INVALID)))
                .verifyComplete();
    }

    @Test
    void mustUpdateFranchiseSuccessfully() {
        FranchiseModel inputModel = FranchiseModel.builder()
                .id(new FranchiseModelId(R2dbcTestConstants.ID_ONE))
                .name(R2dbcTestConstants.FRANCHISE_NAME_UPDATED)
                .build();

        FranchiseData savedData = FranchiseData.builder()
                .id(R2dbcTestConstants.ID_ONE_LONG)
                .name(R2dbcTestConstants.FRANCHISE_NAME_UPDATED)
                .build();

        when(repository.save(any(FranchiseData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.update(inputModel))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals(R2dbcTestConstants.ID_ONE, result.getId().value());
                    assertEquals(R2dbcTestConstants.FRANCHISE_NAME_UPDATED, result.getName());
                })
                .verifyComplete();

        verify(repository).save(any(FranchiseData.class));
    }

    @Test
    void mustFindAllFranchisesSuccessfully() {
        FranchiseData data1 = FranchiseData.builder()
                .id(R2dbcTestConstants.ID_ONE_LONG)
                .name(R2dbcTestConstants.FRANCHISE_NAME_DEFAULT)
                .build();

        FranchiseData data2 = FranchiseData.builder()
                .id(R2dbcTestConstants.ID_TWO_LONG)
                .name(R2dbcTestConstants.FRANCHISE_NAME_UPDATED)
                .build();

        when(repository.findAll()).thenReturn(Flux.just(data1, data2));

        StepVerifier.create(adapter.findAll())
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(R2dbcTestConstants.ID_ONE, result.getId().value());
                    assertEquals(R2dbcTestConstants.FRANCHISE_NAME_DEFAULT, result.getName());
                })
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(R2dbcTestConstants.ID_TWO, result.getId().value());
                    assertEquals(R2dbcTestConstants.FRANCHISE_NAME_UPDATED, result.getName());
                })
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void mustPropagateErrorWhenFindAllFails() {
        RuntimeException dbException = new RuntimeException("Database error");
        when(repository.findAll()).thenReturn(Flux.error(dbException));

        StepVerifier.create(adapter.findAll())
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Database error"))
                .verify();

        verify(repository).findAll();
    }
}
