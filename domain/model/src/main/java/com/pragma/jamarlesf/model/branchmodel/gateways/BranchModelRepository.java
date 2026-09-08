package com.pragma.jamarlesf.model.branchmodel.gateways;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import reactor.core.publisher.Mono;

public interface BranchModelRepository {
    Mono<BranchModel> create(BranchModel branch);
    Mono<BranchModel> findById(BranchModelId id);
    Mono<BranchModel> update(BranchModel branch);
}
