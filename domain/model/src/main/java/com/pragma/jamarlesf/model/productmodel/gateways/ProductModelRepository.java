package com.pragma.jamarlesf.model.productmodel.gateways;

import com.pragma.jamarlesf.model.franchisemodel.FranchiseModelId;
import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductModelRepository {
    Mono<ProductModel> create(ProductModel product);
    Mono<ProductModel> findById(ProductModelId id);
    Mono<ProductModel> update(ProductModel product);
    Flux<ProductModel> findHighestStockByFranchiseId(FranchiseModelId franchiseId);
    Mono<Void> deleteById(ProductModelId id);
}
