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
        if (id == null || id.value() == null || id.value().isBlank()) {
            return Mono.empty();
        }
        return this.findById(Long.valueOf(id.value()));
    }
}
