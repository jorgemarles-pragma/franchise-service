package com.pragma.jamarlesf.r2dbc.franchise;

import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import com.pragma.jamarlesf.r2dbc.helper.ReactiveAdapterOperations;
import com.pragma.jamarlesf.r2dbc.helper.ResilienceOperators;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FranchiseRepositoryAdapter
        extends ReactiveAdapterOperations<FranchiseModel, FranchiseData, Long, FranchiseReactiveRepository>
        implements FranchiseModelRepository {

    private final FranchiseMapper franchiseMapper;
    private final ResilienceOperators resilienceOperators;

    public FranchiseRepositoryAdapter(FranchiseReactiveRepository repository, ObjectMapper mapper, FranchiseMapper franchiseMapper, ResilienceOperators resilienceOperators) {
        super(repository, mapper, franchiseMapper::toModel);
        this.franchiseMapper = franchiseMapper;
        this.resilienceOperators = resilienceOperators;
    }

    @Override
    protected FranchiseData toData(FranchiseModel model) {
        return franchiseMapper.toData(model);
    }

    @Override
    public Mono<FranchiseModel> create(FranchiseModel franchise) {
        return resilienceOperators.apply(this.save(franchise));
    }

    @Override
    public Mono<FranchiseModel> findById(FranchiseModelId id) {
        return resilienceOperators.apply(
                Mono.justOrEmpty(id)
                        .map(FranchiseModelId::value)
                        .filter(val -> !val.isBlank())
                        .map(Long::valueOf)
                        .onErrorResume(NumberFormatException.class, ex -> Mono.empty())
                        .flatMap(this::findById)
        );
    }

    @Override
    public Mono<FranchiseModel> update(FranchiseModel franchise) {
        return resilienceOperators.apply(this.save(franchise));
    }

    @Override
    public Flux<FranchiseModel> findAll() {
        return resilienceOperators.apply(
                repository.findAll().map(franchiseMapper::toModel)
        );
    }
}
