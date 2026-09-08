package com.pragma.jamarlesf.model.branchmodel.gateways;

import com.pragma.jamarlesf.model.branchmodel.BranchModel;
import com.pragma.jamarlesf.model.branchmodel.BranchModelId;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchModelRepository {
    Mono<BranchModel> create(BranchModel branch);
    Mono<BranchModel> findById(BranchModelId id);
    Mono<BranchModel> update(BranchModel branch);
    Flux<BranchModel> findAllByFranchiseId(FranchiseModelId franchiseId);
}
