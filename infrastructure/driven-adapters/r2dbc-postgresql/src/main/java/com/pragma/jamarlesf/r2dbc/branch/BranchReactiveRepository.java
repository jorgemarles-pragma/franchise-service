package com.pragma.jamarlesf.r2dbc.branch;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BranchReactiveRepository
        extends ReactiveCrudRepository<BranchData, Long>, ReactiveQueryByExampleExecutor<BranchData> {
}
