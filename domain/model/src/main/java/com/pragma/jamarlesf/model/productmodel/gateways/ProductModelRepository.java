package com.pragma.jamarlesf.model.productmodel.gateways;

import com.pragma.jamarlesf.model.productmodel.ProductModel;
import reactor.core.publisher.Mono;

public interface ProductModelRepository {
    Mono<ProductModel> create(ProductModel product);
}
