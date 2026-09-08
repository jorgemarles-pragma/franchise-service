package com.pragma.jamarlesf.model.productmodel.gateways;

import com.pragma.jamarlesf.model.productmodel.ProductModel;
import com.pragma.jamarlesf.model.productmodel.ProductModelId;
import reactor.core.publisher.Mono;

public interface ProductModelRepository {
    Mono<ProductModel> create(ProductModel product);
    Mono<ProductModel> findById(ProductModelId id);
    Mono<ProductModel> update(ProductModel product);
}
