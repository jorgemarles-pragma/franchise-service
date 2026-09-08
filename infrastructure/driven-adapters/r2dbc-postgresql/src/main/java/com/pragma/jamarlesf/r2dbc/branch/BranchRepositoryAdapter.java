package com.pragma.jamarlesf.r2dbc.branch;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.branchmodel.gateways.BranchModelRepository;
import com.pragma.jamarlesf.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BranchRepositoryAdapter
        extends ReactiveAdapterOperations<BranchModel, BranchData, Long, BranchReactiveRepository>
        implements BranchModelRepository {

    private final BranchMapper branchMapper;

    public BranchRepositoryAdapter(BranchReactiveRepository repository, ObjectMapper mapper, BranchMapper branchMapper) {
        super(repository, mapper, branchMapper::toModel);
        this.branchMapper = branchMapper;
    }

    @Override
    protected BranchData toData(BranchModel model) {
        return branchMapper.toData(model);
    }

    @Override
    public Mono<BranchModel> create(BranchModel branch) {
        return this.save(branch);
    }

    @Override
    public Mono<BranchModel> findById(BranchModelId id) {
        return Mono.justOrEmpty(id)
                .map(BranchModelId::value)
                .filter(val -> val != null && !val.isBlank())
                .map(Long::valueOf)
                .onErrorResume(NumberFormatException.class, ex -> Mono.empty())
                .flatMap(this::findById);
    }

    @Override
    public Mono<BranchModel> update(BranchModel branch) {
        return this.save(branch);
    }
}
