package com.pragma.jamarlesf.r2dbc.product;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductReactiveRepository
        extends ReactiveCrudRepository<ProductData, Long>, ReactiveQueryByExampleExecutor<ProductData> {
}
