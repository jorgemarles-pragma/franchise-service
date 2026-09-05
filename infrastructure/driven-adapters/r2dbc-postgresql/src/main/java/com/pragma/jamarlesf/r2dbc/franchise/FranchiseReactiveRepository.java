package com.pragma.jamarlesf.r2dbc.franchise;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FranchiseReactiveRepository
        extends ReactiveCrudRepository<FranchiseData, Long>, ReactiveQueryByExampleExecutor<FranchiseData> {
}
