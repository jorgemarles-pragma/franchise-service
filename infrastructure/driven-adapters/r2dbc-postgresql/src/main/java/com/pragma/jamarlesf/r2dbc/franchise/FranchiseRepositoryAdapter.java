package com.pragma.jamarlesf.r2dbc.franchise;

import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.gateways.FranchiseModelRepository;
import com.pragma.jamarlesf.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class FranchiseRepositoryAdapter
        extends ReactiveAdapterOperations<FranchiseModel, FranchiseData, Long, FranchiseReactiveRepository>
        implements FranchiseModelRepository {

    private final FranchiseMapper franchiseMapper;

    public FranchiseRepositoryAdapter(FranchiseReactiveRepository repository, ObjectMapper mapper, FranchiseMapper franchiseMapper) {
        super(repository, mapper, franchiseMapper::toModel);
        this.franchiseMapper = franchiseMapper;
    }

    @Override
    protected FranchiseData toData(FranchiseModel model) {
        return franchiseMapper.toData(model);
    }

    @Override
    public Mono<FranchiseModel> create(FranchiseModel franchise) {
        return this.save(franchise);
    }
}
