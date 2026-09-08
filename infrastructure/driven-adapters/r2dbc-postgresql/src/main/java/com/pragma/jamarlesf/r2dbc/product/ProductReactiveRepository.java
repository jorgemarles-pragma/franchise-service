package com.pragma.jamarlesf.r2dbc.product;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductReactiveRepository
        extends ReactiveCrudRepository<ProductData, Long>, ReactiveQueryByExampleExecutor<ProductData> {

    @Query("SELECT p.id, p.name, p.stock, p.branch_id " +
           "FROM ( " +
           "    SELECT id, name, stock, branch_id, " +
           "           ROW_NUMBER() OVER (PARTITION BY branch_id ORDER BY stock DESC, id ASC) as rn " +
           "    FROM products " +
           "    WHERE branch_id IN (SELECT id FROM branches WHERE franchise_id = :franchiseId) " +
           ") p " +
           "WHERE p.rn = 1")
    Flux<ProductData> findHighestStockByFranchiseId(Long franchiseId);

    Flux<ProductData> findAllByBranchId(Long branchId);
}
