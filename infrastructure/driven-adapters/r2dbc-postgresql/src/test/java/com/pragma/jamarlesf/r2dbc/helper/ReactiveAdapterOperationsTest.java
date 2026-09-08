package com.pragma.jamarlesf.r2dbc.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_ONE;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.ID_TWO;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.TEST_NAME;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.TEST_NAME_ONE;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.TEST_NAME_TWO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class ReactiveAdapterOperationsTest {

    private DummyRepository repository;
    private ObjectMapper mapper;
    private ReactiveAdapterOperations<DummyEntity, DummyData, String, DummyRepository> operations;

    @BeforeEach
    void setUp() {
        repository = mock(DummyRepository.class);
        mapper = mock(ObjectMapper.class);
        operations = new ReactiveAdapterOperations<DummyEntity, DummyData, String, DummyRepository>(
                repository, mapper, DummyEntity::toEntity) {};
    }

    @Test
    void save() {
        DummyEntity entity = new DummyEntity(ID_ONE, TEST_NAME);
        DummyData data = new DummyData(ID_ONE, TEST_NAME);

        when(mapper.map(entity, DummyData.class)).thenReturn(data);
        when(repository.save(data)).thenReturn(Mono.just(data));

        StepVerifier.create(operations.save(entity))
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    void saveAllEntities() {
        DummyEntity entity1 = new DummyEntity(ID_ONE, TEST_NAME_ONE);
        DummyEntity entity2 = new DummyEntity(ID_TWO, TEST_NAME_TWO);
        DummyData data1 = new DummyData(ID_ONE, TEST_NAME_ONE);
        DummyData data2 = new DummyData(ID_TWO, TEST_NAME_TWO);

        when(mapper.map(entity1, DummyData.class)).thenReturn(data1);
        when(mapper.map(entity2, DummyData.class)).thenReturn(data2);
        when(repository.saveAll(any(Flux.class))).thenReturn(Flux.just(data1, data2));

        StepVerifier.create(operations.saveAllEntities(Flux.just(entity1, entity2)))
                .expectNext(entity1, entity2)
                .verifyComplete();
    }

    @Test
    void findById() {
        DummyData data = new DummyData(ID_ONE, TEST_NAME);
        DummyEntity entity = new DummyEntity(ID_ONE, TEST_NAME);

        when(repository.findById(ID_ONE)).thenReturn(Mono.just(data));

        StepVerifier.create(operations.findById(ID_ONE))
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    void findByExample() {
        DummyEntity entity = new DummyEntity(ID_ONE, TEST_NAME);
        DummyData data = new DummyData(ID_ONE, TEST_NAME);

        when(mapper.map(entity, DummyData.class)).thenReturn(data);
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(data));

        StepVerifier.create(operations.findByExample(entity))
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    void findAll() {
        DummyData data1 = new DummyData(ID_ONE, TEST_NAME_ONE);
        DummyData data2 = new DummyData(ID_TWO, TEST_NAME_TWO);
        DummyEntity entity1 = new DummyEntity(ID_ONE, TEST_NAME_ONE);
        DummyEntity entity2 = new DummyEntity(ID_TWO, TEST_NAME_TWO);

        when(repository.findAll()).thenReturn(Flux.just(data1, data2));

        StepVerifier.create(operations.findAll())
                .expectNext(entity1, entity2)
                .verifyComplete();
    }

    static class DummyEntity {
        private String id;
        private String name;

        public DummyEntity(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public static DummyEntity toEntity(DummyData data) {
            return new DummyEntity(data.getId(), data.getName());
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DummyEntity that = (DummyEntity) o;
            return id.equals(that.id) && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    static class DummyData {
        private String id;
        private String name;

        public DummyData(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DummyData that = (DummyData) o;
            return id.equals(that.id) && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    interface DummyRepository extends ReactiveCrudRepository<DummyData, String>, ReactiveQueryByExampleExecutor<DummyData> {}
}
