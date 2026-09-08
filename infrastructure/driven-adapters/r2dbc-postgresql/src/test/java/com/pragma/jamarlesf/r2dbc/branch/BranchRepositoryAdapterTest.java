package com.pragma.jamarlesf.r2dbc.branch;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.r2dbc.helper.ResilienceOperators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.BRANCH_NAME_DEFAULT;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.BRANCH_NAME_UPDATED;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ERROR_DB_TIMEOUT;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_NINE_NINE_NINE;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_NINE_NINE_NINE_LONG;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_NON_NUMERIC;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_ONE;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_ONE_LONG;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_TEN;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_TEN_LONG;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.WHITESPACE_STRING;
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
        adapter = new BranchRepositoryAdapter(repository, mapper, branchMapper, ResilienceOperators.defaultInstance());
    }

    @Test
    void mustCreateBranchSuccessfully() {
        BranchModel inputModel = BranchModel.builder()
                .name(BRANCH_NAME_DEFAULT)
                .franchiseId(new FranchiseModelId(ID_ONE))
                .build();

        BranchData savedData = BranchData.builder()
                .id(ID_TEN_LONG)
                .name(BRANCH_NAME_DEFAULT)
                .franchiseId(ID_ONE_LONG)
                .build();

        when(repository.save(any(BranchData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.create(inputModel))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals(ID_TEN, result.getId().value());
                    assertEquals(BRANCH_NAME_DEFAULT, result.getName());
                    assertNotNull(result.getFranchiseId());
                    assertEquals(ID_ONE, result.getFranchiseId().value());
                })
                .verifyComplete();

        verify(repository).save(any(BranchData.class));
    }

    @Test
    void mustPropagateErrorWhenDatabaseFails() {
        BranchModel inputModel = BranchModel.builder()
                .name(BRANCH_NAME_DEFAULT)
                .franchiseId(new FranchiseModelId(ID_ONE))
                .build();

        RuntimeException dbException = new RuntimeException(ERROR_DB_TIMEOUT);
        when(repository.save(any(BranchData.class))).thenReturn(Mono.error(dbException));

        StepVerifier.create(adapter.create(inputModel))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals(ERROR_DB_TIMEOUT))
                .verify();

        verify(repository).save(any(BranchData.class));
    }

    @Test
    void mustFindBranchByIdSuccessfully() {
        BranchData foundData = BranchData.builder()
                .id(ID_TEN_LONG)
                .name(BRANCH_NAME_DEFAULT)
                .franchiseId(ID_ONE_LONG)
                .build();

        when(repository.findById(ID_TEN_LONG)).thenReturn(Mono.just(foundData));

        StepVerifier.create(adapter.findById(new BranchModelId(ID_TEN)))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(ID_TEN, result.getId().value());
                    assertEquals(BRANCH_NAME_DEFAULT, result.getName());
                    assertEquals(ID_ONE, result.getFranchiseId().value());
                })
                .verifyComplete();

        verify(repository).findById(ID_TEN_LONG);
    }

    @Test
    void mustReturnEmptyWhenBranchNotFound() {
        when(repository.findById(ID_NINE_NINE_NINE_LONG)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(new BranchModelId(ID_NINE_NINE_NINE)))
                .verifyComplete();

        verify(repository).findById(ID_NINE_NINE_NINE_LONG);
    }

    @Test
    void mustReturnEmptyWhenIdIsNull() {
        StepVerifier.create(adapter.findById((BranchModelId) null))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdIsBlank() {
        StepVerifier.create(adapter.findById(new BranchModelId(WHITESPACE_STRING)))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenIdIsNonNumeric() {
        StepVerifier.create(adapter.findById(new BranchModelId(ID_NON_NUMERIC)))
                .verifyComplete();
    }

    @Test
    void mustUpdateBranchSuccessfully() {
        BranchModel inputModel = BranchModel.builder()
                .id(new BranchModelId(ID_TEN))
                .name(BRANCH_NAME_UPDATED)
                .franchiseId(new FranchiseModelId(ID_ONE))
                .build();

        BranchData savedData = BranchData.builder()
                .id(ID_TEN_LONG)
                .name(BRANCH_NAME_UPDATED)
                .franchiseId(ID_ONE_LONG)
                .build();

        when(repository.save(any(BranchData.class))).thenReturn(Mono.just(savedData));

        StepVerifier.create(adapter.update(inputModel))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals(ID_TEN, result.getId().value());
                    assertEquals(BRANCH_NAME_UPDATED, result.getName());
                    assertNotNull(result.getFranchiseId());
                    assertEquals(ID_ONE, result.getFranchiseId().value());
                })
                .verifyComplete();

        verify(repository).save(any(BranchData.class));
    }
}
