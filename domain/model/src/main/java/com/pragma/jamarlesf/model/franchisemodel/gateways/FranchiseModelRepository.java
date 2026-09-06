package com.pragma.jamarlesf.model.franchisemodel.gateways;

import com.pragma.jamarlesf.model.franchisemodel.FranchiseModel;
import reactor.core.publisher.Mono;

public interface FranchiseModelRepository {
    Mono<FranchiseModel> create(FranchiseModel franchise);
}
