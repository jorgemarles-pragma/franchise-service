package com.pragma.jamarlesf.model.franchisemodel.gateways;

import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import reactor.core.publisher.Mono;

public interface FranchiseModelRepository {
    Mono<FranchiseModel> create(FranchiseModel franchise);
    Mono<FranchiseModel> findById(FranchiseModelId id);
    Mono<FranchiseModel> update(FranchiseModel franchise);
}
